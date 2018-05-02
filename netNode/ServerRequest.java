package netNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import db.Db;

/** @brief Class to receive incoming messages and deal with them as needed.
*
*/
class ServerRequest implements Runnable {
	/*PROPERTIES*/
	static final String newDbName = "./NEW_inventory";
	
	static String host = "";
    static int tcpServerPort = 0;

	Socket socket = null;
	NetServer netServer = null;
	Db db = null;
    InputStream in = null;
    OutputStream out = null;
    String intent = "";
    int count = 0;
    StringBuilder sb = new StringBuilder();
    
    /*CONSTRUCTORS*/
    /**
     *  @param netServer the netServer class
     *  @param socket the specific socket connected to
     *  @param count the id of the connected socket
     *  @param db the database object
     *  @param host the host name of the server
     *  @param tcpServerPort the port for the tcp server.
     */
    ServerRequest(NetServer netServer, Socket socket, 
    		           int count, Db db, String host, int tcpServerPort) {
        this.socket = socket;
        this.netServer = netServer;
        this.db = db;
        this.count = count;
        ServerRequest.host = host;
        ServerRequest.tcpServerPort = tcpServerPort;
        
        try {
        	// Create byte stream to dump read bytes into
			in = socket.getInputStream();
			// Create byte stream to read bytes from
			out = socket.getOutputStream();
        } catch (IOException e) {
			e.printStackTrace();
			System.err.println("error getting input or output stream "
			                                + "in SocketServerRequest.");
		}   
    }

    /*THREAD START*/
    /** @brief thread execution begins
    *
    *	listens for the intent of the client socket and
    *   responds appropriately to that intent.
    */
    @Override
    public void run() {
        try {
        	//while the socket is alive
        	while(!socket.isClosed())
        	{
            	/**First we're getting input from 
            	 * the client to see what it wants. **/
                int byteRead = 0;

                //reset stringbuilder buffer
                sb.setLength(0);
                
                // Read from input stream. Note: inputStream.read() will block
                // if no data return
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
                if(intent.compareTo("SendString") == 0){
                	
                	getString();
                	
                }else if(intent.compareTo("SendDatabase") == 0){
                	
                	getDatabase();
                	
                }else if(intent.compareTo("close") == 0){
                	netServer.sendString("close", count);
                	netServer.close(count);
                	closeListener();
                	
                }
        	}

        } catch (IOException e) {
            System.err.println("IOException in SocketServerRequestThread");
        } finally {
            if (!socket.isClosed()) {
                try {
                	socket.close();
                } catch (IOException e){
                    System.err.println("IOException in "
                    		+ "SocketServerRequestThread");
                }
            }
        }
    }

    /*HELPER FUNCTIONS*/
    /** @brief function for processing incoming string input from socket
    *
    *	retrieves string from inputstream of socket and adds it to queue
    *
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
                if (byteRead == 126) {
                    byteRead = -1;
                } else {
                    sb.append((char) byteRead);
                }
            }
	        
            netServer.addStringToQueue(sb.toString());
        	
    	} catch (IOException e) {
            System.err.println("IOException in getString()");
    	}
    }
    
    /** @brief function for processing incoming file input from socket
     * 
     *  retrieves bytes from inputstream and dumps them in a new file.
     *  The new db file is then merged with the old db file.
     *  
     */
    private void getDatabase(){
    	try{
    		//System.out.println("listening for file contents...");
    		
        	//open file
            File file = new File(newDbName + ".mv.db");
            BufferedOutputStream bOut = new BufferedOutputStream(
            		                         new FileOutputStream(file));

            //read in from the socket input stream and 
            //write to file output stream
            int byteRead = 0;
            while (byteRead != -1) {
                byteRead = in.read();
                if (byteRead == 126) {
                    byteRead = -1;
                } else {
                    bOut.write(byteRead);
                }
            }

            bOut.close();
			
			Db new_db = new Db(newDbName, host, tcpServerPort, "", "");
			new_db.copy_contents(db);
			
    	} catch (IOException e) {
            System.err.println("IOException in getDatabase()");
    	}
    }

    /** @brief function to close this particular socket
     * 
     */
    protected void closeListener(){
    	if(!socket.isClosed()){
    		try {
				socket.close();
			} catch (IOException e) {
				System.err.println("IOException attempting to close"
						+ " listenSocket #" + count + ".");
			}
    	}
    }
}
