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
 */
public class NetClient implements Runnable {

	/*PROPERTIES*/
    static final int port = 8080;
    String ip;
    InputStream in = null;
    OutputStream out = null;
    Socket sendSocket = null;
    Socket listenSocket = null;
    ServerSocket serverSocket = null;
    Queue<String> queue = new LinkedList<>();
    NetClientRequest netClientRequest = null;
    Thread netClientRequestThread = null;
    String identity = "";

	/*CONSTRUCTORS*/
    /**
    *  @param ip Server ip address.
    */
    public NetClient(String ip) {
        this.ip = ip;
        try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("IOException creating serverSocket");
		}
    }
	
    /*THREAD START*/
    /** @brief Thread execution begins
    *
    *	open sockets for communication and start
    *   listening for requests from server.
    */
	@Override
    public void run() {

        //create socket
        try {
            //open socket for sending requests to server
        	sendSocket = new Socket(ip, port);
        	in = sendSocket.getInputStream();
            out = sendSocket.getOutputStream();
        	
            //open socket for listening for requests from the server
        	listenSocket = serverSocket.accept();
        	
        	//handles incoming information on socket
        	netClientRequest = new NetClientRequest(this, listenSocket);
        	netClientRequestThread = new Thread(netClientRequest);
        	netClientRequestThread.start();
        	
        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException in socket creation");
            return;
        } catch (IOException e) {
            System.err.println("IOException in socket creation");
            return;
        } 
    }

    /*NETWORK FUNCTIONS*/
	/** @brief sends db through socket
	 *
	 * takes the file pointed to by the db object and sends it through
	 * the socket.
	 *
	 * @param db The database object the file will be extracted from to 
	 *           send across the network.
	 */
    public void sendDB(Db db){
        String intent = "SendDatabase~";
        
        if(out != null){
			try {
				//send intent
				out.flush();
				out.write(intent.getBytes());
				out.flush();
		    	
				File file = new File(db.getFileName() + ".mv.db");
		        byte[] bytes = new byte[(int) file.length()];
		        
		        try{
		        	BufferedInputStream bIn = new BufferedInputStream(new FileInputStream(file));
		        	
		        	//read in from the file
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
					System.err.println("FileNotFoundException in dump()");
	 	        }
				
			} catch (IOException e) {
				System.err.println("IOException in sendDatabase()");
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
		        
		        //send string
		        out.flush();
	        	out.write(str.getBytes());
	        	out.flush();
	        	out.write("~".getBytes());
	        	out.flush();
	        } catch (IOException e){
				System.err.println("IOException in sendString()");
	        }
        }
    }

	/** @brief tells the server to shut down this socket.*/
    public void close(){
    	//telling the server to shutdown this particular client.
    	try {
	        out.flush();
        	out.write("close".getBytes());
        	out.flush();
        	out.write("~".getBytes());
        	out.flush();
	        
        } catch (IOException e){
			System.err.println("IOException in sendString()");
        }
    }

    /*QUEUE FUNCTIONS*/
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
    
    /** @brief adds string parameter to queue*/
    void addStringToQueue(String str){
    	queue.add(str);
    }
    
    /** @brief returns identity
	 *
	 *  @return identity
	 */
    String getIdentity(){
    	return identity;
    }
}