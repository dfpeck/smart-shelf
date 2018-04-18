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


public class NetMat extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    static final int port = 8080;
    String ip;
    InputStream in = null;
    OutputStream out = null;
    Socket sendSocket = null;
    Socket listenSocket = null;
    ServerSocket serverSocket = null;

    public NetMat(String ip) {
        this.ip = ip;
        try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("IOException creating serverSocket");
			e.printStackTrace();
		}
    }
	
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
        	
        	new NetMatRequestThread(listenSocket).start();
        	
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

    //send file through socket.
    public void sendFile(File file){
    	//create request string
        String intent = "SendDatabase~";
        
		try {
			//send intent
			out.flush();
			out.write(intent.getBytes());
			out.flush();
	        System.out.println("sendDatabase intent sent");
	    	
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
    
    //send str through socket
    public void sendString(String str){
    	//create request string
        String intent = "SendString~";
        
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

    public void close(){
    	if (sendSocket != null) {
            try {
            	System.out.println("closing socket");
                sendSocket.close();  
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    	if (listenSocket != null){
        	try {
            	System.out.println("closing socket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    	if (serverSocket != null){
        	try {
            	System.out.println("closing socket");
                listenSocket.close();
                
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException when closing socket...");
                return;
            }
        }
    }

    private class NetMatRequestThread extends Thread {

        private Socket socket;
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        OutputStream out = null;
        String intent = "";

        NetMatRequestThread(Socket socket) {
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

        private void getString(OutputStream out){
        	try{
        		sb.setLength(0);

        		System.out.println("listening for string...");
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
        		
    	        //just print the string for now
                //TODO put string in queue
                System.out.println(sb.toString());
                
        	} catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in reqDatabase");
        	}
        }
        
    }
}