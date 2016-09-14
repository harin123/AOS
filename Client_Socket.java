//package serversocket;

import java.io.*;
import java.net.*;

public class Client_Socket 
{    
    public void clientConnect(String configFile[][], String myMachine, int myPort, String myNeighbors) throws IOException
    {
	int i=0,l,n1=46;
	l=myNeighbors.length();
	while(i<l)
	{
		int n=Character.getNumericValue(myNeighbors.charAt(i))-1;
		if(i+1<l)
                     n1=Character.getNumericValue(myNeighbors.charAt(i+1));
		if(n1==-1||n1==46)       //if next is comma               
                     i=i+2;
                else
                {
                     n=Integer.parseInt(myNeighbors.substring(i,i+2))-1;                           
                     i=i+3;
                }         
		//System.out.println("Sending to "+configFile[n][0]);
		String ClientHost=configFile[n][0]+".utdallas.edu";
		int ClientPort=Integer.parseInt(configFile[n][1]);
		//System.out.println(ClientHost+" "+ClientPort);
		Socket socket = new Socket(ClientHost,ClientPort);
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            	dos.writeUTF("Connect to: "+myMachine+".utdallas.edu-"+myPort+"; From: "+ClientHost+"-"+ClientPort);
		dos.flush();
		//i=i+2;
	}
    }
}
