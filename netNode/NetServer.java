package netNode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import db.Db;
import netNode.StartServerSocket;

public class NetServer implements Runnable {

    IOException ioException;
    UnknownHostException unknownHostException;
    Socket[] socket = null;
    OutputStream[] out = null;
    StartServerSocket startServerSocket = null;
    Db db = null;
    String identity = "";
    int count = 0;

    public NetServer(StartServerSocket startServerSocket, Db db) {
        this.startServerSocket = startServerSocket;
        this.db = db;
    }
	
    public void run() {
		/*starts loop for user input in new thead. Not needed in final implementation, as they'll
		  be calling the functions of their own accord.*/
    	MainServer mainServer = new MainServer();
    	mainServer.main(this, db);
    	
    	System.out.println("exited out of mainServer.main...");
    }
    
    public void setSocket(Socket socket, String client, int num){
        try {
        	System.out.println("getting output stream for NetServer...");
        	this.socket[num] = socket;
        	out[num] = socket.getOutputStream();
        	identity = client;
        	count = num;

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
    
    public String getIdentity(){
    	return identity;
    }
    
    public int getCount(){
    	return count;
    }
    
    public boolean sendString(String str, int num){
    	//create request string
        String intent = "SendString~";
        
        if(out != null){
		    try {
		    	out[num].flush();
		    	out[num].write(intent.getBytes());
		    	out[num].flush();
		        System.out.println("sendString intent sent...");
		        
		        //send string
		        out[num].flush();
		        out[num].write(str.getBytes());
		        out[num].flush();
		        out[num].write("~".getBytes());
		        out[num].flush();
		        System.out.println("string sent: " + str);
		        return true;
		       
		    } catch (IOException e){
				e.printStackTrace();
				System.out.println("IOException in sendString()");
				return false;
		    }
        }else{
        	System.out.println("No socket connected at" + num +".");
        	return false;
        }
    }

    public void close(int num){
    	if(socket[num] != null){
    		try {
				socket[num].close();
				System.out.print("Closed socket #" + num + ".");
			} catch (IOException e) {
				System.err.print("IOException closing socket #" + num + ".");
			}
    	}
    }
    
    public void exit(){
    	if(count <= 0){
    		System.out.println("No sockets to close.");
    	}
    	for(int i = count; i > 0; i--){
    		close(i);
    	}
    	startServerSocket.closeServer();
    }
    
    public String pop(){
    	return startServerSocket.pop();
    }
}