//package raymond;

import java.io.*;
import java.net.*;
import java.util.*;

public class Ray_protocol extends Thread 
{
	ServerSocket serverSocket;
	static String Neighbors=Server_Socket.STNeighbors;	
	static String Parentptr=Server_Socket.STParent;		// Holder
	//static String root;					// self 
	static String ReqNode;					// Requester (child node)
	static String myNode=Server_Socket.myMachine;
	static int myPort=Server_Socket.myPort;
	static int myAppPort=Server_Socket.AppPort;
	String message;
	String sp[]=new String[20];
	boolean Token = false;		// Privilege
	boolean RequestSent = false;	// Asked
	boolean UsingCS =false;
	int count=0;
	static int neighcount ;
	int port;
	RequestQ q = new RequestQ(neighcount +1);
	public static HashMap<String,Integer> hostport = new HashMap<String,Integer>();

	public void RaymondMain()  {

		try{
		if(Parentptr.equals("")){
			Token=true;
			Parentptr=myNode;
			String home = System.getProperty("user.home");
			String filepath = home+"/ce6378_aos_project2/result.txt";
			File file = new File(filepath);
			file.createNewFile();
		}
		Parentptr=Parentptr.substring(0,4);
                System.out.println("Raymonds on Machine: "+myNode+", Port: "+myPort+", ST Neighbors: "+Neighbors+", Parent: "+Parentptr);
		//putting neighbors into a HashMap
		String spi[]=Neighbors.split(",");
	      	String spi1[]=new String[2];
      		for ( String ss : spi) 
	      	{
      			count++;
          		spi1=ss.split("-");
			hostport.put(spi1[0],Integer.parseInt(spi1[1]));
			//System.out.println(hostport.get(spi1[0]),spi1[1]);
      		}      
      		neighcount = count;
//		System.out.println("Token of : "+myNode+" is "+Token);
		// listens to cs-enter sent from application
		serverSocket = new ServerSocket(myPort);
//		System.out.println("created server socket");
		serverSocket.setSoTimeout(100000);
//		System.out.println("set the timeeout");
		while(true)
		{
			Socket socket = serverSocket.accept();
			//System.out.println("Raymond's Listening on: "+myNode+"-"+myPort);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			String str = in.readUTF();
			System.out.println (str);
			sp=str.split("\\s");
			if(sp[1].trim().equals("cs-enter"))
			{
				// msg - "sent cs-enter message"
				//System.out.println("request for cs received");
				ReqNode=myNode;
				q.enqueue(ReqNode);
				
				if(Token==true)
				{
					System.out.println(myNode+" I have the token");
					RequestSent=true;
					Assign_Token();
				}
				else
				{
					System.out.println(myNode+" sending token request to " + Parentptr);
					Make_Request();	
				}
			}			
			if((sp[1].trim()).equals("TokenRequest"))
			{		// msg - "sent TokenRequest message"
				ReqNode=sp[4].trim();
				System.out.println("Token Request received from" +ReqNode);
				q.enqueue(ReqNode);
				if(Token==true)
				{
					System.out.println(myNode+" I have the token");
					Assign_Token();
				}
				else
				{
					System.out.println(myNode+" sending token request to " + Parentptr);
					Make_Request();
				}			
			}
			if((sp[1].trim()).equals("Token"))
			{			// msg - "Sent Token message"
				System.out.println("Received the Token ");
				Parentptr=myNode;
				Token=true;
				Assign_Token();
				Make_Request();	
			}
			if((sp[1].trim()).equals("cs-exit"))
			{			// msg - "sent cs-exit message"
				System.out.println("Completed cs execution");
				UsingCS=false;
				Assign_Token();
				Make_Request();			
			}
		    }//while ends
		}catch(SocketTimeoutException s){
              System.out.println("Socket has timed out");

		}catch(Exception e){e.printStackTrace();}
	}

	private void Assign_Token() throws UnknownHostException, IOException, InterruptedException, Exception
	{					// sends token to requester
		
		if((Parentptr.equals(myNode))&&(UsingCS==false)&&(!q.isEmpty()))
		{
			Parentptr=q.dequeue();
			RequestSent=false;
		
			if(Parentptr.equals(myNode))
			{
				UsingCS=true;
				// initiate entry into CS
				Socket socket = new Socket(myNode+".utdallas.edu",myAppPort);
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
          			dos.writeUTF("Token sent by System Module: "+myNode);
				dos.flush();
				System.out.println(myNode+": I am in critical section");
				//contact application layer to exe cs				
			}
			else{
				Token=false;
				// Send Token to the current parentptr 
				System.out.println("Sending token to child "+Parentptr);
				Socket socket = new Socket(Parentptr+".utdallas.edu",hostport.get(Parentptr));
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                      		out.writeUTF("Sent Token message" + Parentptr);
			}
		}
		
	}

	private void Make_Request() throws UnknownHostException, IOException{		// Makes a request to its parent for token
		if((Parentptr!=myNode)&&(!q.isEmpty())&&(RequestSent==false))
		{
			//System.out.println("Parentptr: "+Parentptr+" , port: "+hostport.get(Parentptr));
			Socket socket = new Socket(Parentptr+".utdallas.edu",hostport.get(Parentptr));
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("Sent TokenRequest message from "+myNode);
			out.flush();
			RequestSent=true;
		}
	}
}
