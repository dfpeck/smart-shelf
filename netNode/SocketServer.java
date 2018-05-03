package netNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import db.Db;
import netNode.ServerRequest;

/** @brief Class for listening for new connections
*
*	listens for connections to serverSocket and creates
*	threads dedicated to sending and receiving from the new
*	connected sockets.
*/
class SocketServer implements Runnable {
	/*PROPERTIES*/
	static final int serverSocketPort = 8080;
	
	static String host = "";
    static int tcpServerPort = 0;
	
    int count = 0;
    boolean listen = true;
    NetServer netServer = null;
    ServerSocket serverSocket = null;
    Db db = null;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<ServerRequest> serverRequests = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<Thread> serverRequestThreads = new Vector();
    
    /*CONSTRUCTORS*/
    /** 
     * @param netServer the nerServer
     * @param db the db object
     * @param host the server host name
     * @param tcpServerPort port number to the tcp server
     */
    SocketServer(NetServer netServer, Db db, String host, int tcpServerPort){
    	this.netServer = netServer;
    	this.db = db;
    	SocketServer.host = host;
    	SocketServer.tcpServerPort = tcpServerPort;
    }
    
    /*GETTERS*/
    public int getPort(){
    	return serverSocketPort;
    }
    
    /*THREAD START*/
    /** @brief Thread execution begins
    *
    *	listen for new connections to the serverSocket.
    *	create new threads for sending and receiving responses
    *	from this client and make these known to netServer.
    */
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(serverSocketPort);
            
            OutputStream out = null;
            InputStream in = null;
            
            StringBuilder sb = new StringBuilder();

            while (listen == true) {
            	try{
                    // block the call until connection is created and return
                    // Socket object for mat/ui -> server communication
                    Socket listenSocket = serverSocket.accept();

                    System.out.println("#" + count + " from "
                    		             + listenSocket.getInetAddress() + ":"
                    		             + listenSocket.getPort());

                    //Now creating second socket for 
                    //server -> mat/ui communication
                    Socket sendSocket = new Socket(
                    		 listenSocket.getInetAddress(), serverSocketPort);
                    
                    /**find identity of socket**/
                    out = sendSocket.getOutputStream();
                    in = sendSocket.getInputStream();
                    String intent = "GetIdentity " + count + "~";
                    
                    try {
                    	out.flush();
                    	out.write(intent.getBytes());
                    	out.flush();
            	        
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
            			System.err.println("IOException in getIdentity");
                    }
                    
                    /**start up send and receive threads**/
                    serverRequests.add(count, new ServerRequest(
                    		netServer, listenSocket, count, db, host,
                    		tcpServerPort));
                    serverRequestThreads.add(count, new Thread(
                    		                serverRequests.get(count)));
                    serverRequestThreads.get(count).start();
                    
                    System.out.println(sb.toString() + " connected.");
                    netServer.setSocket(sendSocket, sb.toString(), count);
                    count++;
                    
            	} catch (SocketException e){
            		System.err.println("Interrupted SocketServer");
            	}
            }
        } catch (IOException e) {
        	System.err.println("IOException in SocketServer");
        }
        for(int i = 0; i < serverRequestThreads.size(); i++)
        {
        	serverRequests.get(i).closeListener();
        }
    }
    
    /*HELPER FUNCTIONS*/
    /** @brief stop listening for connections to serverSocket*/
    public void closeServer() {
    	listen = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("IOException in closing socket");
            }
        } 
    }
}
