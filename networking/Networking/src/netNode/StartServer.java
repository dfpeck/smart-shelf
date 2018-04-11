package netNode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class StartServer {
    ServerSocket serverSocket;
    static final int socketServerPort = 8080;

    public StartServer() {
        SocketServerThread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPort;
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
                serverSocket = new ServerSocket(socketServerPort);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Socket socket = serverSocket.accept();
                    System.out.println("accepted socket...");
                    count++;
                    System.out.println("#" + count + " from "
                    		               + socket.getInetAddress() + ":"
                    		               + socket.getPort());

                    System.out.println("attempting to run request thread...");
                    SocketServerRequestThread request = new SocketServerRequestThread(socket, count);
                    request.start();
                    
                    //create NetServer object in new thread so you can query the mat or UI
                    new NetServer(socket);

                }
            } catch (IOException e) {
            	System.out.println("IOException in SocketServerThread");
                e.printStackTrace();
            }
        }
    }

    private class SocketServerRequestThread extends Thread {

        private Socket hostThreadSocket;
        //int cnt;
        StringBuilder sb = new StringBuilder();
        String[][] fakeDatabase;

        SocketServerRequestThread(Socket socket, int c) {
        	System.out.println("socket thread constructor...");
            hostThreadSocket = socket;
            //cnt = c;
            //fake database that i'll be pulling from. Will need to edit code to interface with real database API
            fakeDatabase = new String[][]{
	                {"Bucket of bolts", "10lb"},
	                {"Box of Nails", "5lb"},
	                {"Cup of Screws", "2lb"}
            };
        }

        @Override
        public void run() {
            try {
            	/**First we're getting input from the client to see what it wants. **/
                // Create byte stream to dump read bytes into
                InputStream in = hostThreadSocket.getInputStream();

                int byteRead = 0;

                // Read from input stream. Note: inputStream.read() will block
                // if no data return
                System.out.println("attempting to read in from socket...");
                while (byteRead != -1) {
                    byteRead = in.read();
                    if (byteRead == 126){
                        byteRead = -1;
                    }else {
                        sb.append((char) byteRead);
                    }
                }
                
                in.close();

                //split the front and back of the string into item ID and purpose
                String[] request = sb.toString().split(" ");

                //check for errors in user input
                if(Integer.parseInt(request[0]) > fakeDatabase.length){
                    request[0] = "";
                    request[1] = "";

                    // send response
                    System.out.println("outputting response to socket...");
                    OutputStream out = hostThreadSocket.getOutputStream();
                    out.write(("Number exceeds entries in database(" + fakeDatabase.length + ").~").getBytes());
                    out.flush();

                    System.out.println("User entered number too large for database.");

                }

                /** then checking and responding **/
                // compare lexigraphically since bytes will be different
                if(request[1].compareTo("ReqDatabase") == 0){
                	
                	reqDatabase(hostThreadSocket);
                	
                }else if(request[1].compareTo("DumpDatabase") == 0){
                	
                	dumpDatabase(hostThreadSocket);
                	
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException in SocketServerRequestThread"
                        + e.toString());
            } finally {
                if (hostThreadSocket != null) {
                    try {
                    	System.out.println("closing socket...");
                        hostThreadSocket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("IOException in SocketServerRequestThread"
                                + e.toString());
                    }
                }
            }
        }
    }

    private void reqDatabase(Socket hostThreadSocket){
    	try{
    		System.out.println("outputting response to socket...");
    
	        //get file from external storage
	        File file = new File("C:\\smart-shelf\\networking\\javaServer", "database.txt");
	
	        byte[] bytes = new byte[(int) file.length()];
	        BufferedInputStream bIn;
	
	        //read in from the file
	        bIn = new BufferedInputStream(new FileInputStream(file));
	        bIn.read(bytes, 0, bytes.length);
	
	        //output on socket
	        OutputStream out = hostThreadSocket.getOutputStream();
	        out.write(bytes, 0, bytes.length);
	        out.flush();
	        out.write("~".getBytes());
	        out.flush();
	
	        bIn.close();
	        System.out.println("Requested Database");
    	} catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException in reqDatabase");
    	}
    }
    
    private void dumpDatabase(Socket hostThreadSocket){
    	try{
    		System.out.println("listening for file contents...");
    		InputStream in = hostThreadSocket.getInputStream();
    		
        	//open file
            File file = new File("C:\\smart-shelf\\networking\\javaClient", "database.txt");
            //will need to increase size of byte array if information exceeds 1024 bytes
            byte[] bytes = new byte[1024];
            BufferedOutputStream bOut = new BufferedOutputStream(new FileOutputStream(file));

            //read in from the socket input stream and write to file output stream
            int bytesRead = in.read(bytes, 0, bytes.length);
            bOut.write(bytes, 0, bytesRead);
            
            //closing stream objects
            bOut.close();
            in.close();
    	} catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException in dumpDatabase");
    	}
    }
}

