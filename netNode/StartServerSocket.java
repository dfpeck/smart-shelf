package netNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import db.Db;

public class StartServerSocket {
    ServerSocket serverSocket;
    static final int serverSocketPort = 8080;
    Queue<String> queue = new LinkedList<>();
	static final String NEW_DATABASE_FILE_NAME = "NEW_inventory";
	static final String DATABASE_FILE_NAME = "inventory";
	NetServer netServer = null;
	Thread netServerThread = null;
	SocketServer socketServer = null;
	Thread socketServerThread = null;
	Db db = null;
	boolean listen = true;
	

    public StartServerSocket(Db db) {
    	this.db = db;
    	netServer = new NetServer(this, db);
    	netServerThread = new Thread(netServer);
        netServerThread.start();
    	socketServer = new SocketServer(netServer);
    	socketServerThread = new Thread(socketServer);
    	socketServerThread.start();    
    }

    public int getPort() {
        return serverSocketPort;
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
    
    public void closeSender(int count){
    	netServer.close(count);
    }
    
    public String pop(){
    	if(queue.isEmpty())
    	{
    		return "empty";
    	}else{
    		return queue.remove();
    	}
    }

    // Creates a thread that listens on a port for incoming connects and
    // instantiates a listener thread for each of the connections requested.
    private class SocketServer implements Runnable {

        int count = 0;
        NetServer netServer = null;
        @SuppressWarnings({ "unchecked", "rawtypes" })
		Vector<StartServerRequest> startServerRequests = new Vector();
        @SuppressWarnings({ "unchecked", "rawtypes" })
		Vector<Thread> startServerRequestThreads = new Vector();
        
        SocketServer(NetServer netServer){
        	this.netServer = netServer;
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
	
	                    //Now creating second socket for server -> mat/ui communication
	                    Socket sendSocket = new Socket(listenSocket.getInetAddress(), serverSocketPort);
	                    //System.out.println("created socket for sending requests...");
	                    
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
	
	                		// Read from input stream. Note: inputStream.read() will block
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
	                    //System.out.println("attempting to run request thread...");
	                    startServerRequests.add(count, new StartServerRequest(listenSocket, count));
	                    startServerRequestThreads.add(count, new Thread(startServerRequests.get(count)));
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
    }

    private class StartServerRequest implements Runnable {

    	StringBuilder sb = new StringBuilder();
    	Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        String intent = "";
        int count = 0;
        
        StartServerRequest(Socket socket, int count) {
        	//System.out.println("startServerRequest constructor...");
            this.socket = socket;
            try {
            	// Create byte stream to dump read bytes into
				in = socket.getInputStream();
				// Create byte stream to read bytes from
				out = socket.getOutputStream();
            } catch (IOException e) {
				e.printStackTrace();
				System.err.println("error getting input or output stream in SocketServerRequest.");
			}
            this.count = count;
        }

        @Override
        public void run() {
        	//System.out.println("StartServerRequest run()...");
            try {
            	//while the socket is alive
            	while(!socket.isClosed())
            	{
	            	/**First we're getting input from the client to see what it wants. **/
	                int byteRead = 0;
	
	                // Read from input stream. Note: inputStream.read() will block
	                // if no data return
	                //reset stringbuilder buffer
	                sb.setLength(0);
	                
	                //System.out.println("attempting to read intent...");
	                while (byteRead != -1) {
	                    byteRead = in.read();
	                    if (byteRead == 126){
	                        byteRead = -1;
	                    }else {
	                        sb.append((char) byteRead);
	                    }
	                }
	                intent = sb.toString();
	                //System.out.println(intent);
	                
	                /** then checking and responding **/
	                // compare lexigraphically since bytes will be different
	                if(intent.compareTo("SendString") == 0){
	                	
	                	getString(out);
	                	
	                }else if(intent.compareTo("SendDatabase") == 0){
	                	
	                	getDatabase(in);
	                	
	                }else if(intent.compareTo("close") == 0){
	                	netServer.sendString("close", count);
	                	closeListener();
	                	closeSender(count);
	                	
	                }
            	}

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IOException in SocketServerRequestThread"
                        + e.toString());
            } finally {
                if (!socket.isClosed()) {
                    try {
                    	//System.out.println("closing socket...");
                    	socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.err.println("IOException in SocketServerRequestThread"
                                + e.toString());
                    }
                }
            }
            //System.out.println("exited StartServerRequest loop for socket #" + count + "...");
        }
    
        //retrieves string representation of record and sends it back through the socket outputstream.
        private void getString(OutputStream out){
        	try{
        		//reset stringbuilder buffer
                sb.setLength(0);
                
            	//System.out.println("listening for string...");
            	// Read from input stream. Note: inputStream.read() will block
                // if no data return
                int byteRead = 0;
                while (byteRead != -1) {
                    byteRead = in.read();
                    if (byteRead == 126) {
                        byteRead = -1;
                    } else {
                        sb.append((char) byteRead);
                    }
                }
    	        
                //add string to queue
                queue.add(sb.toString());
            	
        	} catch (IOException e) {
                e.printStackTrace();
                System.err.println("IOException in getString()");
        	}
        }
        
        //writes to the file from the inputstream
        private void getDatabase(InputStream in){
        	try{
        		//System.out.println("listening for file contents...");
        		
            	//open file
                File file = new File(NEW_DATABASE_FILE_NAME + ".mv.db");
                BufferedOutputStream bOut = new BufferedOutputStream(new FileOutputStream(file));

                //read in from the socket input stream and write to file output stream
                int byteRead = 0;
                while (byteRead != -1) {
                    byteRead = in.read();
                    if (byteRead == 126) {
                        byteRead = -1;
                    } else {
                        bOut.write(byteRead);
                    }
                }

                //closing stream objects
                bOut.close();
				
				Db new_db = new Db(NEW_DATABASE_FILE_NAME);
				new_db.copy_contents(db);
        	} catch (IOException e) {
                e.printStackTrace();
                System.err.println("IOException in getDatabase()");
        	}
        }
    
        private void closeListener(){
        	if(!socket.isClosed()){
        		try {
					socket.close();
					//System.out.println("closeListener: closed socket #" + count + ".");
				} catch (IOException e) {
					System.err.println("IOException attempting to close listenSocket #" + count + ".");
				}
        	}
        }
    }
    
    // This finds the IP address that the socket is hosted on, so the server's IP/port
    // For all network interfaces and all IPs connected to said interfaces print
    // those that are site local addresses (an address which doesn't have the
    // global prefix and thus is only on this network).
    public String getIpAddress() {
        String ip = "";
        try {
            // Enumaration consisting of all interfaces on this machine
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();

                // For the specific networkInterface create an enumeration
                // consisting of all IP addresses on said interface
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();

                // Rotate through all IP addresses on the networkInterface and
                // print the IP if the IP is a SiteLocalAddress.
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "IOException in getIpAddress" + e.toString() + "\n";
        }
        return ip;
    }  
}

