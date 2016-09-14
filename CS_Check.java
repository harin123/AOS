//package test;
import java.io.*;

public class CS_Check
{
	public static int NoOfLines() 
    	{
		int noOfLines=0;
		try{
            	FileReader fr=new FileReader("./result.txt");
            	BufferedReader br=new BufferedReader(fr);
            	String aLine;

	        while((aLine=br.readLine())!= null)
        	{
                	noOfLines++;
            	}
            	br.close();
		}catch(Exception e){e.printStackTrace();}
            	return noOfLines;
    	}
        public static void main(String args[]) 
        {
                try {
			int i;
			//Reading configuration file
        		FileReader fr=new FileReader("./result.txt");
        		BufferedReader br=new BufferedReader(fr);
			int NumberOfLines=NoOfLines();        
        		for(i=0;i<NumberOfLines;i=i+2)
        		{
        			if(br.readLine().substring(0,8).equals("Entering")){
					if(br.readLine().substring(0,7).equals("Exiting")){}
                			else{ 
						System.out.println("Error in CS Exceution");
						break;}
				}
        		}	 
			if(i==NumberOfLines)               
			System.out.println("Successful, i: "+i);
                } catch ( Exception e ) {e.printStackTrace();}
        }
}
