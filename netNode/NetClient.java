package netNode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import db.Db;

/** @brief Class for connecting and interacting with server.
 *
 *	After connecting to the server functions become available for use.
 *  Also creates a thread for receiving requests from the server.
 *
 *  @param ip Server ip address.
 */
public class NetClient implements Runnable {

	/*Properties*/
    IOException ioException;
    UnknownHostException unknownHostException;
    static final int port = 8080;
    String ip;
    InputStream in = null;
    OutputStream out = null;
    Socket sendSocket = null;
    Socket listenSocket = null;
    ServerSocket serverSocket = null;
    Queue<String> queue = new LinkedList<>();
    NetMatRequest netMatRequest = null;
    Thread netMatRequestThread = null;
    String identity = "";

	/*Constructors*/
    public NetClient(String ip) {
        this.ip = ip;
        try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("IOException creating serverSocket");
			e.printStackTrace();
		}
    }
	
	/*Thead execution start*/
	@Override
    public void run() {

        //create socket
        try {
        	System.out.println("creating sockets...");
            //open socket for sending requests to server
        	sendSocket = new Socket(ip, port);
        	in = sendSocket.getInputStream();
            out = sendSocket.getOutputStream();
        	
            //open socket for listening for requests from the server
        	listenSocket = serverSocket.accept();
        	
        	netMatRequest = new NetMatRequest(listenSocket);
        	netMatRequestThread = new Thread(netMatRequest);
        	netMatRequestThread.start();
        	
        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            System.out.println("UnknownHostException in socket creation");
            return;
        } catch (IOException e) {
            this.ioException = e;
            System.out.println("IOException in socket creation");
            return;
        } 
    }

    /*Network Functions*/
	/** @brief sends db through socket
	 *
	 * takes the file pointed to by the db object and sends it through
	 * the socket.
	 *
	 * @param db The database object the file will be extracted from to 
	 *           send across the network.
	 */
    public void sendDB(Db db){
    	//create request string
        String intent = "SendDatabase~";
        
        if(out != null){
			try {
				//send intent
				out.flush();
				out.write(intent.getBytes());
				out.flush();
		        System.out.println("sendDatabase intent sent");
		    	
				File file = new File(db.getFileName() + ".mv.db");
				
				//byte array with size of the file 
		        byte[] bytes = new byte[(int) file.length()];
		        
		        //read in from the file
		        try{
		        	BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(file));
		        	
		        	bIn.read(bytes, 0, bytes.length);
		        	
			        //output on socket
		        	out.flush();
		        	out.write(bytes, 0, bytes.length);
		        	out.flush();
		        	out.write("~".getBytes());
		        	out.flush();
			
			        bIn.close();
		        } catch (FileNotFoundException e)
	 	        {
	 	        	e.printStackTrace();
					System.out.println("FileNotFoundException in dump()");
	 	        }
				
		        System.out.println("Sent Database");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IOException in sendDatabase()");
			}
        }
    }
    
    /** @brief sends string through socket
	 *
	 * @param str The string to send to the server.
	 */
    public void sendString(String str){
    	//create request string
        String intent = "SendString~";
        
        if(out != null){
	        try {
	        	//send intent
	        	out.flush();
	        	out.write(intent.getBytes());
	        	out.flush();
		        System.out.println("sendString intent sent...");
		        
		        //send string
		        out.flush();
	        	out.write(str.getBytes());
	        	out.flush();
	        	out.write("~".getBytes());
	        	out.flush();
		        System.out.println("string sent: " + str);
		        
	        } catch (IOException e){
				e.printStackTrace();
				System.out.println("IOException in sendString()");
	        }
        }
    }

	/** @brief closes the socket
	 *
	 *  closes the socket on the client side.
	 */
    public void close(){
    	//telling the server to shutdown this particular client.
    	sendString("close");
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			System.err.println("InterruptedException in close()");
		}
    	if (!sendSocket.isClosed()) {
            try {
            	System.out.println("closing sendSocket");
                sendSocket.close();  
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    	if (!listenSocket.isClosed()){
        	try {
            	System.out.println("closing listenSocket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    	if (!serverSocket.isClosed()){
        	try {
            	System.out.println("closing serverSocket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    	System.out.println("All sockets closed");
    }

	/** @brief returns top of the string retrieved queue
	 *
	 *  @return "empty" if queue is empty. String at top of queue if not empty.
	 */
    public String pop(){
    	if(queue.isEmpty())
    	{
    		return "empty";
    	}else{
    		return queue.remove();
    	} 	
    }
    
	/** @brief Helper class for retrieving requests from connected sockets
	 *
	 *  @param socket The socket that will be listened on.
	 */
    private class NetMatRequest implements Runnable {

		/*Properties*/
        private Socket socket;
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        OutputStream out = null;
        String intent = "";

		/*Constructors*/
        NetMatRequest(Socket socket) {
        	System.out.println("NetMatRequestThread constructor...");
            this.socket = socket;
            
            try {
				this.in = socket.getInputStream();
				this.out = socket.getOutputStream();
			} catch (IOException e) {
				System.out.println("IOException in NetMatRequestThread constructor...");
				e.printStackTrace();
			}
        }

		/*Thead execution start*/
        @Override
        public void run() {
        	while(!socket.isClosed()){
            	/**First we're getting input from the client to see what it wants. **/
                int byteRead = 0;

                //reset stringbuilder buffer
                sb.setLength(0);
                
				// Read from input stream. Note: inputStream.read() will block
                // if no data return
                try{
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
	                	getString();
	                }else if(intent.compareTo("GetIdentity") == 0){
	                    try {        	        
	            	        out.flush();
	            	        out.write(identity.getBytes());
	            	        out.flush();
	            	        System.out.println("Identity sent");
	                    } catch (IOException e){
	            			e.printStackTrace();
	            			System.out.println("IOException sending identity");
	                    }
	                }
                }catch(IOException e){
                	System.out.println("It's likely that the server went offline, dumping socket...");
                	try {
                		socket.close();
                		System.out.println("Socket closed");
                	} catch (IOException e1){
                		System.out.println("IOException closing socket in NetMatRequest.");
                	}
                }
        	}
            if (!socket.isClosed()) {
                try {
                	System.out.println("closing socket...");
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("IOException closing socket in NetMatRequest"
                            + e.toString());
                }
            }
            close();
        }

		/** @brief Helper function for reading a string from the socket
		 *
		 *  Reads in string from the socket and adds it to the queue that
		 *  the user can retrieve later.
		 */
        private void getString(){
        	try{
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
        		
                //add string to queue
                queue.add(sb.toString());
				System.out.println("Retrieved string.");
                
        	} catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in reqDatabase" + e.toString());
        	}
        }
        
    }
}