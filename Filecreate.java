//package test;
import java.io.*;

public class Filecreate 
{
	static String text="";

	public Filecreate(String msg)
	{
		text=msg;
	}
	public void addText() throws IOException
	{
		try {        	
			//user.home gives the current directory
        		String home = System.getProperty("user.home");
			// directory to be specified.
			String filepath = home+"/ce6378_aos_project2/result.txt";
        		//System.out.println("file path:" + home);
   		        File file = new File(filepath);
			if(file.exists() && !file.isDirectory())
                        {
				BufferedWriter output = new BufferedWriter(new FileWriter(file,true));
                        	output.write(text + "\n");
                        	output.close();
			}
			else
			{
				file.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(file));
                        	output.write(text + "\n");
                        	output.close();
			}
        	} catch ( Exception e ) {e.printStackTrace();}
	}
}

