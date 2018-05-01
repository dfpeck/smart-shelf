package netNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import db.Db;
import netNode.StartServerRequest;

//Creates a thread that listens on a port for incoming connects and
// instantiates a listener thread for each of the connections requested.
class SocketServer implements Runnable {
	static final int serverSocketPort = 8080;
	
	static String host = "";
    static int tcpServerPort = 0;
	
    int count = 0;
    boolean listen = true;
    NetServer netServer = null;
    ServerSocket serverSocket = null;
    Db db = null;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<StartServerRequest> startServerRequests = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<Thread> startServerRequestThreads = new Vector();
    
    SocketServer(NetServer netServer, Db db, String host, int tcpServerPort){
    	this.netServer = netServer;
    	this.db = db;
    	SocketServer.host = host;
    	SocketServer.tcpServerPort = tcpServerPort;
    }
    
    @Override
    public void run() {
        try {
            // create ServerSocket using specified port
            serverSocket = new ServerSocket(serverSocketPort);
            OutputStream out = null;
            InputStream in = null;
            StringBuilder sb = new StringBuilder();

            while (listen == true) {
            	try{
                    // block the call until connection is created and return
                    // Socket object for mat/ui -> server communication
                    Socket listenSocket = serverSocket.accept();
                    //System.out.println("accepted socket...");
                    System.out.println("#" + count + " from "
                    		             + listenSocket.getInetAddress() + ":"
                    		             + listenSocket.getPort());

                    //Now creating second socket for 
                    //server -> mat/ui communication
                    Socket sendSocket = new Socket(
                    		 listenSocket.getInetAddress(), serverSocketPort);
                    
                    /*find identity of socket*/
                    out = sendSocket.getOutputStream();
                    in = sendSocket.getInputStream();
                    String intent = "GetIdentity~";
                    
                    try {
                    	out.flush();
                    	out.write(intent.getBytes());
                    	out.flush();
            	        //System.out.println("GetIdentity intent sent...");
            	        
        				//reset stringbuilder buffer
                		sb.setLength(0);

                		// Read from input stream. 
                		// Note: inputStream.read() will block
                        // if no data return
                		int byteRead = 0;
                        while (byteRead != -1) {
                            byteRead = in.read();
                            if (byteRead == 126){
                                byteRead = -1;
                            }else {
                                sb.append((char) byteRead);
                            }
                        }           	        
                    } catch (IOException e){
            			e.printStackTrace();
            			System.err.println("IOException in getIdentity");
                    }
                    
                    /*start up send and receive threads*/
                    startServerRequests.add(count, new StartServerRequest(
                    		netServer, listenSocket, count, db, host,
                    		tcpServerPort));
                    startServerRequestThreads.add(count, new Thread(
                    		                startServerRequests.get(count)));
                    startServerRequestThreads.get(count).start();
                    
                    System.out.println(sb.toString() + " connected.");
                    netServer.setSocket(sendSocket, sb.toString(), count);
                    count++;
            	} catch (SocketException e){
            		System.err.println("Interrupted SocketServer");
            	}
            }
        } catch (IOException e) {
        	System.err.println("IOException in SocketServer");
            e.printStackTrace();
        }
        for(int i = 0; i < startServerRequestThreads.size(); i++)
        {
        	startServerRequests.get(i).closeListener();
        }
        //System.out.println("exited socketServer loop...");
    }
    
    public void closeServer() {
    	listen = false;
    	//System.out.println("listen == false");
        if (serverSocket != null) {
            try {
                serverSocket.close();
                //System.out.println("Closed serverSocket");
            } catch (IOException e) {
                System.err.println("IOException in closing socket");
            }
        }
        
    }
    
    public int getPort(){
    	return serverSocketPort;
    }
}
