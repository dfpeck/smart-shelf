package netNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/** @brief Helper class for retrieving requests from connected sockets*/
class NetClientRequest implements Runnable {

   /*PROPERTIES*/
   private NetClient netClient = null;
   private Socket socket = null;
   StringBuilder sb = new StringBuilder();
   InputStream in = null;
   OutputStream out = null;
   String intent = "";
   String[] intentSplit = null;
   boolean flag = true;

	/*CONSTRUCTORS*/
   /**
    *  @param netClient the parent class
    *  @param socket the client socket
    */
   NetClientRequest(NetClient netClient, Socket socket) {
	   this.netClient = netClient;
       this.socket = socket;
       
       try {
			this.in = socket.getInputStream();
			this.out = socket.getOutputStream();
		} catch (IOException e) {
			System.err.println("IOException in NetMatRequestThread constructor...");
		}
   }

   /*THREAD START*/
   /** @brief Thread execution begins
   *
   *	Listen for server intent and respond appropriately
   *	to that intent.
   */
   @Override
   public void run() {
	   while(flag){
		   /**First we're getting input from the client to see what it wants. **/
           int byteRead = 0;

           //reset stringbuilder buffer
           sb.setLength(0);
           
           // Read from input stream. Note: inputStream.read() will block
           // if no data return
           try{
               while (byteRead != -1) {
                   byteRead = in.read();
                   if (byteRead == 126){
                       byteRead = -1;
                   }else {
                       sb.append((char) byteRead);
                   }
               }
               intent = sb.toString();
               intentSplit = intent.split(" ");

               /** then checking and responding **/
               if(intentSplit[0].compareTo("SendString") == 0){
            	   getString();
               	
               }else if(intentSplit[0].compareTo("GetIdentity") == 0){
                   try {       
                   netClient.setId(Integer.parseInt(intentSplit[1]));	   
           	       out.flush();
           	       out.write(netClient.getIdentity().getBytes());
           	       out.flush();
           	        
                   } catch (IOException e){
                	   System.err.println("IOException sending identity");
                   }
                   
               }else if(intent.compareTo("close") == 0){
            	   flag = false;
               }
               
           }catch(IOException e){
        	   System.err.println("It's likely that the server went offline, dumping socket...");
           		try {
           			socket.close();
           			flag = false;
           		} catch (IOException e1){
           			System.err.println("IOException closing socket in NetMatRequest.");
           		}
           }
   	}
       if (!socket.isClosed()) {
           try {
               socket.close();
               flag = false;
           } catch (IOException e){
               System.err.println("IOException closing socket in NetMatRequest");
           }
       }
   }

   /*HELPER FUNCTIONS*/
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
   		
        netClient.addStringToQueue(sb.toString());
           
   		} catch (IOException e) {
           System.err.println("IOException in reqDatabase" + e.toString());
   		}
   }
}
