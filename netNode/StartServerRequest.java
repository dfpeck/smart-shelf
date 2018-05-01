package netNode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import db.Db;

class StartServerRequest implements Runnable {
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
    
    StartServerRequest(NetServer netServer, Socket socket, 
    		           int count, Db db, String host, int tcpServerPort) {
    	//System.out.println("startServerRequest constructor...");
        this.socket = socket;
        this.count = count;
        this.db = db;
        StartServerRequest.host = host;
        StartServerRequest.tcpServerPort = tcpServerPort;
        
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

    @Override
    public void run() {
    	//System.out.println("StartServerRequest run()...");
        try {
        	//while the socket is alive
        	while(!socket.isClosed())
        	{
            	/**First we're getting input from 
            	 * the client to see what it wants. **/
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
                	netServer.close(count);
                	closeListener();
                	
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
                    System.err.println("IOException in "
                    		+ "SocketServerRequestThread");
                }
            }
        }
    }

    //retrieves string representation of record and sends it back
    //through the socket outputstream.
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
            netServer.addStringToQueue(sb.toString());
        	
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

            //closing stream objects
            bOut.close();
			
			Db new_db = new Db(newDbName, host, tcpServerPort, "", "");
			new_db.copy_contents(db);
    	} catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException in getDatabase()");
    	}
    }

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
