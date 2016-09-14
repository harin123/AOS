//package serversocket;
import java.io.*;
import java.net.*;


public class Server_Socket extends Thread 
{
    ServerSocket serverSocket;

    static int NumberOfLines=NoOfLines();
    static String configFile[][]=new String[NumberOfLines][4];
    static boolean positiveAck=false; 
    static String STNeighbors="";
    static int myPort=0;
    static String myMachine="";
    static String STParent="";
    static int nodeCount=0;
    static boolean forwardSTMsg=false;
    static int STNeighborCount=0;
    static int machineCount=0;
    static int checkPoint=0;
    static String myNeighbors="";
    static int AppPort=0;    

    public void run(){
	int k=0,i=0;
        while(true){
            try{
            Socket socket = serverSocket.accept();
//            System.out.println("Listening on: "+myMachine+"-"+myPort);
	    DataInputStream dis = new DataInputStream(socket.getInputStream());
            String str = dis.readUTF();
            System.out.println(str);
	    int portCheck;

	//creating spanning tree neighbors
	    if(str.substring(0,4).equals("Okay"))
	    {
		STNeighbors=STNeighbors.concat(str.substring(11,15)+"-"+str.substring(29,33)+",");
	   	STNeighborCount++;
	    }
	    portCheck=Integer.parseInt(configFile[0][1]);
	    if((str.substring(0,4).equals("Okay")||str.substring(0,4).equals("Node"))&&!configFile[0][0].equals(myMachine)&&portCheck!=myPort)
            {
		String ClientHost=STParent.substring(0,4)+".utdallas.edu";
		int ClientPort=Integer.parseInt(STParent.substring(5,9));
                Socket socketC = new Socket(ClientHost,ClientPort);
		DataOutputStream dos = new DataOutputStream(socketC.getOutputStream());
            	dos.writeUTF("Node joined ST");
		dos.flush();
            }
//	    System.out.println("Node count: "+nodeCount+"; No of Line: "+NumberOfLines);
	     if((str.substring(0,4).equals("Okay")||str.substring(0,4).equals("Node"))&&configFile[0][0].equals(myMachine)&&portCheck==myPort)
            {
		nodeCount++;
	    }
//	    System.out.println("Node count: "+nodeCount+"; No of Line: "+(NumberOfLines-1));
	    if(positiveAck==false)
	    {
		positiveAck=true;
		String sendMachine=str.substring(12,29);
		int sendPort=Integer.parseInt(str.substring(30,34));
		String fromMachine=str.substring(42,59);
                int fromPort=Integer.parseInt(str.substring(60,64));
		//System.out.println("Reply to: "+sendMachine+"-"+sendPort+"; From: "+fromMachine+"-"+fromPort);
		Socket socket1 = new Socket(sendMachine,sendPort);
		DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
                dos.writeUTF("Okay from: "+fromMachine+"-"+fromPort);		
		STNeighbors=STNeighbors.concat(sendMachine.substring(0,4)+"-"+sendPort+",");
		STNeighborCount++;
		STParent=sendMachine.substring(0,4)+"-"+sendPort;
		System.out.println("ST Parent of "+myMachine+"-"+myPort+": "+STParent);
		System.out.println("Number of Line: "+NumberOfLines);
		String NList="",newNList="";
		int sendingNeighbor=0,len=0;
		for(i=0;i<NumberOfLines;i++)
		{
			//connect request came from
			if((configFile[i][0].equals(sendMachine.substring(0,4))) && sendPort==Integer.parseInt(configFile[i][1]))
				sendingNeighbor=i+1;
			if((configFile[i][0].equals(fromMachine.substring(0,4))) && fromPort==Integer.parseInt(configFile[i][1]))
				NList=configFile[i][2];
		}
		System.out.println("NList as copied: "+NList);
		len=NList.length();
		i=0;
		int n1=50;
//		System.out.println("Sending Neighbor: "+sendingNeighbor);
		while(i<len)
		{
			n1=50;
			int n=Character.getNumericValue(NList.charAt(i));
			if(i+1<len)
				n1=Character.getNumericValue(NList.charAt(i+1));
			if(n1==-1||n1==50)
				i=i+2;
			else
			{
				n=Integer.parseInt(NList.substring(i,i+2));
				i=i+3;
			}
			//System.out.println("n: "+n);
			if(n!=sendingNeighbor)
			{
				newNList=newNList.concat(n+",");
			}
		}
		System.out.println("newNList before truncating: "+newNList);
		newNList=newNList.substring(0,newNList.length()-1);
		fromMachine=fromMachine.substring(0,4);
		Client_Socket cs=new Client_Socket();
		cs.clientConnect(configFile,fromMachine,fromPort,newNList);
		}//if posAck loop ends

		if((str.substring(0,8).equals("Spanning")||nodeCount==(NumberOfLines-1))&&(forwardSTMsg==false))
		{
			forwardSTMsg=true;
			Thread.sleep(1000);
			forwardMessage("Spanning tree is ready ",STParent);
			System.out.println("Spanning Tree Neighbors for "+myMachine+"-"+myPort+" is: "+STNeighbors);
			socket.close();
			serverSocket.close();
			break;
		}

            }catch(SocketTimeoutException s){
		System.out.println("Socket has timed out");
		break;	    
            }catch(Exception e){
                e.printStackTrace();
		break;
            }    
        }//while ends
	try{	
		Thread.sleep(10000);
	}catch(Exception e){e.printStackTrace();}
	System.out.println(myMachine+"-"+myPort+" while has ended. Socket closed!");
	try{
		Socket socket1 = new Socket(myMachine+".utdallas.edu",AppPort);
		DataOutputStream dos = new DataOutputStream(socket1.getOutputStream());
        	dos.writeUTF("Spanning Tree Ready - sent from "+myMachine+"-"+myPort);
		dos.flush();
	}catch(Exception e){e.printStackTrace();}
	Ray_protocol rp=new Ray_protocol();
	rp.RaymondMain();
    }
    public Server_Socket(int portNumber){
        try{

        serverSocket = new ServerSocket(portNumber);
        serverSocket.setSoTimeout(100000);
        }catch(Exception e ){
	    System.out.println("Port in use");
		connectToPort();
          //  e.printStackTrace();
        }
    }
public static void connectToPort()
{
	System.out.println("Entering connectToPort function");
	int i;
	for(i=checkPoint+1;i<NumberOfLines;i++)
        {
                if(configFile[i][0].equals(myMachine))
                {
                        checkPoint=i;
                        myPort=Integer.parseInt(configFile[i][1]);
                        Server_Socket ss1=new Server_Socket(myPort);
                        ss1.start();
                        myNeighbors=configFile[i][2];
			AppPort=Integer.parseInt(configFile[i][3]);
			System.out.println(myMachine+"'s Port: "+myPort);
			System.out.println(myMachine+"'s Neighbors: "+myNeighbors);
		}
	}
}
public static void forwardMessage(String msg, String sendingNeighbor) throws Exception
	{
		 int l=STNeighbors.length();
                 int i=0;
                 while(i<l)
                 {
                 	String ClientHost=STNeighbors.substring(i,i+4)+".utdallas.edu";
                        int ClientPort=Integer.parseInt(STNeighbors.substring(i+5,i+9));
			String ClientHostPort=ClientHost.substring(0,4)+"-"+ClientPort;
			//System.out.println("CLientHostPort: "+ClientHostPort);
			if(!ClientHostPort.equals(sendingNeighbor))
			{
                        	Socket socketC = new Socket(ClientHost,ClientPort);
                        	DataOutputStream dos = new DataOutputStream(socketC.getOutputStream());
                        	dos.writeUTF(msg+myMachine+"-"+myPort);
                        	dos.flush();
			}
                        i=i+10;
                 }
	}

public static int NoOfLines() 
    {
	int noOfLines=0;
	try{
            FileReader fr=new FileReader("./configuration.txt");
            BufferedReader br=new BufferedReader(fr);
/*            String aLine;

            while((aLine=br.readLine())!= null)
            {
                noOfLines++;
            }*/
	    noOfLines=Integer.parseInt(br.readLine());
            br.close();
	}catch(IOException e){e.printStackTrace();}
            return noOfLines;
    }

public static void main(String[] args) throws Exception 
    {
       	int i,conv,k;
	String fullHostName=InetAddress.getLocalHost().getHostName();
        i=fullHostName.indexOf(".");
        myMachine=fullHostName.substring(0,i);
     //   System.out.println("My Machine Name: "+myMachine);        

	//Reading configuration file
        FileReader fr=new FileReader("./configuration.txt");
        BufferedReader br=new BufferedReader(fr);
	NumberOfLines=Integer.parseInt(br.readLine());
	System.out.println("NoL: "+NumberOfLines);
	configFile=new String[NumberOfLines][4];
	String perLine[]=new String[NumberOfLines];
        for(i=0;i<NumberOfLines;i++)
        {
        	perLine[i]=br.readLine();
                k=0;
                for (String temp: perLine[i].split("-", 4))
                {
                	configFile[i][k++]=temp;
                }
        }
	//String eli;
	br.readLine();
	br.readLine();
	br.readLine();
	for(i=0;i<NumberOfLines;i++)
        {
		configFile[i][3]=br.readLine();
	}
	br.close();
	for(i=0;i<NumberOfLines;i++)
	{
		System.out.println(configFile[i][0]+" "+configFile[i][1]+" "+configFile[i][2]+" "+configFile[i][3]);
	}
	boolean p=false;
        for(i=0;i<NumberOfLines;i++)
        {
		if(configFile[i][0].equals(myMachine)&&(p==false))
		{  
			//System.out.println("Entered if at i: "+i);
			p=true;
			checkPoint=i;
			myPort=Integer.parseInt(configFile[i][1]);
			Server_Socket ss=new Server_Socket(myPort);
			//ss=new Server_Socket(myPort);
			ss.start();
			myNeighbors=configFile[i][2];
			AppPort=Integer.parseInt(configFile[i][3]);
			System.out.println(myMachine+"'s Port: "+myPort);
			System.out.println(myMachine+"'s Neighbors: "+myNeighbors);
			System.out.println(myMachine+"'s App Port: "+AppPort); 
		}
        	//System.out.println(configFile[i][0]+" "+configFile[i][1]+" "+configFile[i][2]+" "+configFile[i][3]);
        }
	
	//Spanning Tree node search begins	
	if(configFile[0][0].equals(myMachine))
	{
		positiveAck=true;
		//Call Client function/class
		Thread.sleep(5000);
		Client_Socket cs=new Client_Socket();
		cs.clientConnect(configFile,myMachine,myPort,myNeighbors);
	}
    }
}
