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


public class StartServer {
    ServerSocket serverSocket;
    static final int serverSocketPort = 8080;

    public StartServer() {
        SocketServerThread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    public int getPort() {
        return serverSocketPort;
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in closing socket");
            }
        }
    }

    // Creates a thread that listens on a port for incoming connects and
    // instantiates a listener thread for each of the connections requested.
    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(serverSocketPort);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object for mat/ui -> server communication
                    Socket listenSocket = serverSocket.accept();
                    System.out.println("accepted socket...");
                    count++;
                    System.out.println("#" + count + " from "
                    		               + listenSocket.getInetAddress() + ":"
                    		               + listenSocket.getPort());

                    //Now creating second socket for server -> mat/ui communication
                    Socket sendSocket = new Socket(listenSocket.getInetAddress(), serverSocketPort);
                    System.out.println("created socket for sending requests...");
                    
                    //listening for requests
                    System.out.println("attempting to run request thread...");
                    new StartServerRequestThread(listenSocket).start();
                    
                    //create NetServer object in new thread so you can query the mat or UI
                    System.out.println("creating NetServer object...");
                    new NetServer(sendSocket).start();

                }
            } catch (IOException e) {
            	System.out.println("IOException in SocketServerThread");
                e.printStackTrace();
            }
        }
    }

    private class StartServerRequestThread extends Thread {

        private Socket socket;
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        OutputStream out = null;
        String intent = "";
		Db db = null;

        StartServerRequestThread(Socket socket) {
        	System.out.println("socket thread constructor...");
            this.socket = socket;
            try {
            	// Create byte stream to dump read bytes into
				in = socket.getInputStream();
				// Create byte stream to read bytes from
				out = socket.getOutputStream();
            } catch (IOException e) {
				e.printStackTrace();
				System.out.println("error getting input or output stream in SocketServerRequestThread.");
			}
        }

        @Override
        public void run() {
            try {
            	//while the socket is alive
            	while(socket != null)
            	{
	            	/**First we're getting input from the client to see what it wants. **/
	                int byteRead = 0;
	
	                // Read from input stream. Note: inputStream.read() will block
	                // if no data return
	                //reset stringbuilder buffer
	                sb.setLength(0);
	                
	                System.out.println("attempting to read intent...");
	                while (byteRead != -1) {
	                    byteRead = in.read();
	                    if (byteRead == 126){
	                        byteRead = -1;
	                    }else {
	                        sb.append((char) byteRead);
	                    }
	                }
	                intent = sb.toString();
	
	                /** then checking and responding **/
	                // compare lexigraphically since bytes will be different
	                if(intent.compareTo("SendString") == 0){
	                	
	                	getString(out);
	                	
	                }else if(intent.compareTo("SendDatabase") == 0){
	                	
	                	getDatabase(in);
	                	
	                }
            	}

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in SocketServerRequestThread"
                        + e.toString());
            } finally {
                if (socket != null) {
                    try {
                    	System.out.println("closing socket...");
                    	socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("IOException in SocketServerRequestThread"
                                + e.toString());
                    }
                }
            }
        }
    
        //retrieves string representation of record and sends it back through the socket outputstream.
        private void getString(OutputStream out){
        	try{
        		//reset stringbuilder buffer
                sb.setLength(0);
                
            	System.out.println("listening for string...");
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
    	        
                //just print the string for now
                //TODO put string in queue
                System.out.println(sb.toString());
            	
        	} catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in getString()");
        	}
        }
        
        //writes to the file from the inputstream
        private void getDatabase(InputStream in){
        	try{
        		System.out.println("listening for file contents...");
        		
            	//open file
                File file = new File("netNode\\NewDatabase.db");
                //will need to increase size of byte array if information exceeds 1024 bytes
                byte[] bytes = new byte[1024];
                BufferedOutputStream bOut = new BufferedOutputStream(new FileOutputStream(file));

                //read in from the socket input stream and write to file output stream
                int bytesRead = in.read(bytes, 0, bytes.length);
                bOut.write(bytes, 0, bytesRead);
                
                //closing stream objects
                bOut.close();
				
				db.setFile(file);
				System.out.println("set db.file to received file");
                
                System.out.println("wrote to file");
        	} catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in getDatabase()");
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

