package netNode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import db.Db;
import netNode.StartServerSocket;

/** @brief Main driver for server functions.
 * 
 *  Maintains itself as a holder for the sockets connected
 *  to the server and supplies functions for sending messages
 *  to the clients and retrieving the strings that have been
 *  retrieved elsewhere and added to the queue
 * 
 */
class NetServer implements Runnable {
	/*Properties*/ 
    UnknownHostException unknownHostException;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<Socket> socket = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<OutputStream> out = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<String> identity = new Vector();
    
    StartServerSocket startServerSocket = null;
    SocketServer socketServer = null;
    Db db = null;
    
    int count = -1;
    
	Queue<String> queue = new LinkedList<>();

	/*Constructors*/
	/**
	 * @param startServerSocket the startServerSocket class
	 * @param db the db object
	 */
    NetServer(StartServerSocket startServerSocket, Db db) {
        this.startServerSocket = startServerSocket;
        this.db = db;
    }
    
    /*Getters*/
    String getIdentity(int num){
    	return identity.get(num);
    }
    
    int getCount(){
    	return count;
    }
    
    /*Setters*/
    void setSocketServer(SocketServer socketServer){
    	this.socketServer = socketServer;
    }
	
    /** @brief Thread execution begins
     * 
     * 	starts the MainServer object for interaction with this class.
     * 
     */
    public void run() {
    	MainServer mainServer = new MainServer();
    	mainServer.main(this, db);
    }
    
    /** @brief sets up new sockets
     * 
     * 	@param socket socket to add
     *  @param client ui or mat depending on the client that connects
     *  @param num the id associated with the client.
     * 
     */
    void setSocket(Socket socket, String client, int num){
        try {
        	this.socket.add(num, socket);
        	out.add(num, socket.getOutputStream());
        	identity.add(num, client);
        	count = num;

        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException in socket creation");
            return;
        } catch (IOException e) {
            System.err.println("IOException in socket creation");
            return;
        }
    }
    
    /** @brief sends string to specific client
     * 
     * 	@param str message to send
     *  @param num the id of the client to send to
     * 
     */
    boolean sendString(String str, int num){
        String intent = "SendString~";
        
        if(str != "close"){
	        if(out != null){
			    try {
			    	//send intent
			    	out.get(num).flush();
			    	out.get(num).write(intent.getBytes());
			    	out.get(num).flush();
			        
			        //send string
			        out.get(num).flush();
			        out.get(num).write(str.getBytes());
			        out.get(num).flush();
			        out.get(num).write("~".getBytes());
			        out.get(num).flush();

			        return true;
			       
			    } catch (IOException e){
					System.err.println("IOException in sendString()");
					return false;
			    }
	        }else{
	        	System.out.println("No socket connected at" + num +".");
	        	return false;
	        }
        }else{
        	try {
    	        out.get(num).flush();
            	out.get(num).write(str.getBytes());
            	out.get(num).flush();
            	out.get(num).write("~".getBytes());
            	out.get(num).flush();
    	        return true;
    	        
            } catch (IOException e){
    			System.err.println("IOException in sendString()");
    			return false;
            }
        }     
    }

    /** @brief pop string from front of queue
     * 
     * 	@return the front of the queue or `null` if queue is empty
     * 
     */
    String pop(){
    	if(queue.isEmpty())
    	{
    		return null;
    	}else{
    		return queue.remove();
    	}
    }
    
    /** @brief add string to queue*/
    void addStringToQueue(String str){
    	queue.add(str);
    }
    
    /** @brief close specific socket
     * 
     * 	@param num the socket to close
     * 
     */
    void close(int num){
    	if(!socket.get(num).isClosed()){
    		try {
    			identity.set(num, "OFFLINE");
    			socket.get(num).close();

				//System.out.println("netServer: Closed socket #" + num + ".");
				//System.out.println("identity of #" + num + " set to " + identity.get(num));
			} catch (IOException e) {
				System.err.println("IOException closing socket #" + num + ".");
			}
    	}
    }
    
    /** @brief for all sockets: close them. Also close server.*/
    void exit(){
    	if(count < 0){
    		System.out.println("No sockets to close.");
    	}
    	for(int i = count; i >= 0; i--){
    		sendString("close", i);
    		close(i);	
    	}
    	socketServer.closeServer();
    }
}
