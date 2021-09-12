package external.sort;
// @author samyar rahimi

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class ExternalSort 
{
    public static void main(String[] args) 
    {    
        Scanner in = new Scanner(System.in);
        System.out.println("file e input ra moshakhas konid manande : \nD:\\Test\\inputfile.txt");
        String inputfilepath = in.next();
        
        System.out.println("masire ijade chunk ha va file e output ra moshakhas konid (chunk ha delete NAKHAHAND shod) manande :\nD:\\Test\\");
        String chunkandoutputfilepath = in.next();
        in.close();
        
        File inputfile = new File(inputfilepath);
        long start = 0;
        int all=0;
        int chunksize=0;
        //moshakhas kardane tedad adad dar har chunk be in surta hast ke
        //agar tedad kolle adad bar 1024*1024*1024/53 bakhshpazir nabashad
        // az in meghdar kam mikonim ta kol bar size bakhshpazir bashad
        //dar natije har chunk meghdar mosavi adad khahad dasht.
        
        //start = System.currentTimeMillis();
        //PutsRandomLongsinFile(all, inputfile);
        //write kardane file ba long haye random be tedade all be surate string dar file
        //agar file e input nadarim xodeman be all adad dade file e input ra misazim
        //System.out.println("Zamane ijad e file e input : "+(System.currentTimeMillis()-start));

        BufferedReader inputreader;
        start = System.currentTimeMillis();
        try {
            inputreader = new BufferedReader(new FileReader(inputfile));
            all = filesize(inputreader);//file e input chand adad darad.
            inputreader.close();
        } catch (IOException ex) {
            System.out.println("IO Exception dar khandane tedad e file e input");
        }
        System.out.println("Zamane shomareshe tedad adad dar file e input : "+(System.currentTimeMillis()-start));
        chunksize = chunksize(all);//har chunk chand adad dashte bashad ta RAM e masrafi kamtar az 1 gig shavad.
        System.out.println(chunksize);
        System.gc();
        
        int c =  all/chunksize ,counter=0,i=1;
        File [] files = new File[c+1];
        long[] arr;
        
        try
        {
            BufferedReader bfreader = new BufferedReader(new FileReader(inputfile));
            String line;
            start = System.currentTimeMillis();
            while( i <= c )
            {
                //size of array
                arr = new long[ chunksize ];
                counter = 0;
                System.gc();
                //xandane adad az file va ezafe kardane be arraye
                do{
                    line = bfreader.readLine();
                    if(line == null)
                        break;
                    arr[counter]=Long.parseLong(line);
                    counter++;
                }
                while( counter < arr.length);

                //sort kardane arraye
                Arrays.sort(arr);

                files[i] = new File(chunkandoutputfilepath+"chunk"+i+".txt");
                BufferedWriter bfwriter = new BufferedWriter(new FileWriter(files[i]));
                
                //nevehstane ghesmate sort shode be file e i
                counter = 0 ;
                while(counter < arr.length )
                {
                    bfwriter.write(arr[counter]+"\n");
                    counter++;
                }
                bfwriter.close();  
                arr = null;
                System.gc();
                i++;
                
            }
            bfreader.close();
            System.out.println("Zamane ijade chunk haye sort shode: "+(System.currentTimeMillis()-start));
        }
        catch(IOException e) {
            System.out.println("Io");
        }
        arr = null;
        System.gc();
        // ijade chunk haye sort shode tamam shod
        ////////////////////////////////////////////////////////////////////////
        // shuru e merge kardan e chunk ha va ijade file e output
        
        BufferedReader[] bfreaders = new BufferedReader[c+1];//baraye har file(chunk) reader e khase an file(chunk)
        long [] nums = new long[c+1];
        //arraye ke har onsore an minimum adad an file hast
        //baraye mesal nums[1] minimum adade file[1] hast
        //             nums[2] minimum adade file[2] hast
        // har addad dar arrayeye nums kuchektarin addad dar filee[i] hast.
        // be in shekl mitavan fahmid ke kuchektarin addad dar nums kodam hast va bad az 
        // ezafe kardane an be output 2vomin addad az haman file ra jaygozaine 
        // an addad garar dad.
        //long array is like -> nums[1] , nums[2]
        //                         |        |
        //                      file[1]   file[2]
        
        try 
        {
            //baraye har file yek reader
            for(int j=1;j<bfreaders.length;j++)
                bfreaders[j] = new BufferedReader(new FileReader(files[j]));
            
            File OutputFile = new File(chunkandoutputfilepath+"outputfile.txt");
            
            BufferedWriter OutputWriter = new BufferedWriter(new FileWriter(OutputFile));
            
            boolean [] isFinished = new boolean[c+1];
            
            InitialNums(nums, bfreaders);//initial karde nums[] ba avvalin adad dar har file
            int minIndex;
            System.gc();
            start = System.currentTimeMillis();
            while(HanuzFilehaTamumNashodan(isFinished))
            {
                minIndex = FindMinNumPlace(nums,isFinished);
                OutputWriter.write(nums[minIndex]+"\n");
                InitialMinWithSecondMinInThatFile(nums, bfreaders[minIndex], minIndex,isFinished);
            }
            OutputWriter.close();
        }
        catch(IOException e)
        {
            System.out.println("Io 2");
        }
        System.out.println("Zamane merge kardan : "+(System.currentTimeMillis()-start));
    }
    
    //in method dar surati ke hameye file ha khande shode bashand true barmigardanad
    public static boolean HanuzFilehaTamumNashodan(boolean [] isFinished) throws IOException
    {
        for (int i = 1; i < isFinished.length; i++) {
            if(isFinished[i] == false)
               return true;
        }
        return false;
    }
    
    //initial kardane nums be surati ke ba az bardashte min az nums 
    //adade badi dar an file ra be nums ezafe konim
    public static void InitialMinWithSecondMinInThatFile(long[] nums,BufferedReader bfreader,int whichFile,boolean [] isFinished)
            throws IOException
    {
        String min2 = bfreader.readLine();
        if(min2 != null)
        { 
            long t = Long.parseLong(min2);
            nums[whichFile] = t;
        }
        else{
            isFinished[whichFile] = true;
            //agar min2 null shod yani file digar tamam shode va be akhare file residim
            //pas isFinished e an file ra true mikonim.
        }
    }
    
    //peyda kardane makane minimum dar nums
    public static int FindMinNumPlace(long [] arr,boolean [] isFinished)
    {
        long m = Long.MAX_VALUE;
        int j=1;
        for(int i=1;i<arr.length;i++)
            if(isFinished[i] == false  && m >= arr[i] ){
                m = arr[i];
                j = i;
            }
        return j;
    }
    
    public static void InitialNums(long[] nums,BufferedReader[] bfreaders) throws IOException
    {
        for (int i = 1; i < nums.length; i++) {
           nums[i] = Long.parseLong(bfreaders[i].readLine());
        }
    }
    
    public static void PutsRandomLongsinFile(long l,File file)
    {
        SecureRandom r = new SecureRandom();
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            for(int j=0;j<l;j++)
            {
                out.write(r.nextLong()+"\n");
            }
            out.close();
        }
        catch ( IOException e)
        {
            System.out.println("IO Exception dar CreateFile");
        }
    }
    public static int filesize(BufferedReader input) {
        int c=0;
        try {
            while (input.readLine()!=null) {
                c++;
            }
        }catch (IOException e){
            System.out.println(e.toString());
        }
        return c;
    }
    public static int chunksize(int numbersInFile) {
        int size=1024*1024*1024/53;
        System.out.println(size);
        while(numbersInFile%size != 0)
            size--;
        return size;//in tedad adad be surate string neveshte shode
    }
}