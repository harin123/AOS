//package application;

import java.io.*;
import java.net.*;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

public class AppModuleClient extends Thread 
{	
	ServerSocket serverSocket;
	
	static String fullHostName="";
	static int SystemPort=0;
	static int myAppPort=0;
	static int NoOfCSRequests=0;
	static int CSDelay=0;
	static int InCS=0;
	static int count=1;
	private Mutex lock=new Mutex();

public AppModuleClient()
{
	try{

        serverSocket = new ServerSocket(myAppPort);
        serverSocket.setSoTimeout(120000);
        }catch(Exception e){ e.printStackTrace();}
}
	
public static void main(String[] args) throws IOException
  {
	int i;
	fullHostName=InetAddress.getLocalHost().getHostName();
        i=fullHostName.indexOf(".");
        String myMachine=fullHostName.substring(0,i);

	//Reading configuration file
        FileReader fr=new FileReader("./configuration.txt");
        BufferedReader br=new BufferedReader(fr);
	int NumberOfLines=Integer.parseInt(br.readLine());
	String configFile[][]=new String[NumberOfLines][2];	
	for(i=0;i<NumberOfLines;i++)
               	configFile[i][0]=br.readLine();
	NoOfCSRequests=Integer.parseInt(br.readLine());
        CSDelay=Integer.parseInt(br.readLine());
        InCS=Integer.parseInt(br.readLine());
	for(i=0;i<NumberOfLines;i++){
                configFile[i][1]=br.readLine();
		if(configFile[i][0].substring(0,4).equals(myMachine))
			myAppPort=Integer.parseInt(configFile[i][1]);
	}
	System.out.println("My App port that I will listen on: "+myAppPort);
	AppModuleClient amc=new AppModuleClient();
	amc.start();
}

public void run()
{
	while(true)
	{
	  try{

		Socket socket = serverSocket.accept();
		System.out.println("Application module listening on "+fullHostName+" , "+myAppPort);
		DataInputStream dis = new DataInputStream(socket.getInputStream());
	        String str = dis.readUTF();
        	System.out.println(str);
		if((str.substring(0,8)).equals("Spanning"))
		{
			Thread.sleep(2000);
			SystemPort=Integer.parseInt(str.substring(37,41));
			//System.out.println("System port: "+SystemPort);
			Socket socket1 = new Socket(fullHostName,SystemPort);
			DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
        		dos.writeUTF("send cs-enter request");
			dos.flush();
		}
		if((str.substring(0,5)).equals("Token"))
		{
			String csEnter="Entering CS - "+fullHostName+" , "+myAppPort+" , "+(System.currentTimeMillis());
			Filecreate fc=new Filecreate(csEnter);
			fc.addText();
			System.out.println(csEnter);
			// goes to  application layer
			lock.acquire();
			CriticalSection cs = new CriticalSection();
			lock.release();
			// till here goes to application layer
			Thread.sleep(InCS); //executing CS by sleeping
			String csExit="Exiting CS - "+fullHostName+" , "+myAppPort+" , "+(System.currentTimeMillis());
			Filecreate fc1=new Filecreate(csExit);
			fc1.addText();
			System.out.println(csExit);
			Socket socket2 = new Socket(fullHostName,SystemPort);
                        DataOutputStream dos = new DataOutputStream(socket2.getOutputStream());
                        dos.writeUTF("send cs-exit request");
                        dos.flush();
			if(count<NoOfCSRequests)
			{
				Thread.sleep(CSDelay);//after sending cs-exit, wait for 50 msec before next cs-enter request
				Socket socket3 = new Socket(fullHostName,SystemPort);
				DataOutputStream dos1 = new DataOutputStream(socket3.getOutputStream());
				dos1.writeUTF("send cs-enter request");
                        	dos1.flush();
				count++;
			}
		}
	  }catch(SocketTimeoutException s){
		System.out.println("Socket has timed out");
		break;
	  }catch(Exception e){
		e.printStackTrace();
		break;}
	}
  }

public static int NoOfLines() 
    {
	int noOfLines=0;
	try{
            FileReader fr=new FileReader("./configuration.txt");
            BufferedReader br=new BufferedReader(fr);

	    noOfLines=Integer.parseInt(br.readLine());
            br.close();
	}catch(IOException e){e.printStackTrace();}
            return noOfLines;
    }
}
