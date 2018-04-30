package netNode;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import db.Db;
import netNode.StartServerSocket;

public class NetServer implements Runnable {

    IOException ioException;
    UnknownHostException unknownHostException;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<Socket> socket = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<OutputStream> out = new Vector();
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Vector<String> identity = new Vector();
    StartServerSocket startServerSocket = null;
    Db db = null;
    int count = -1;

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
        	this.socket.add(num, socket);
        	out.add(num, socket.getOutputStream());
        	identity.add(num, client);
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
    
    public String getIdentity(int num){
    	return identity.get(num);
    }
    
    public int getCount(){
    	return count;
    }
    
    public boolean sendString(String str, int num){
    	//create request string
        String intent = "SendString~";
        
        if(out != null){
		    try {
		    	out.get(num).flush();
		    	out.get(num).write(intent.getBytes());
		    	out.get(num).flush();
		        System.out.println("sendString intent sent...");
		        
		        //send string
		        out.get(num).flush();
		        out.get(num).write(str.getBytes());
		        out.get(num).flush();
		        out.get(num).write("~".getBytes());
		        out.get(num).flush();
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
    	if(!socket.get(num).isClosed()){
    		try {
    			identity.set(num, "OFFLINE");
    			socket.get(num).close();

				System.out.println("netServer: Closed socket #" + num + ".");
				System.out.println("identity of #" + num + " set to " + identity.get(num));
			} catch (IOException e) {
				System.err.println("IOException closing socket #" + num + ".");
			}
    	}
    }
    
    public void exit(){
    	if(count < 0){
    		System.out.println("No sockets to close.");
    	}
    	for(int i = count; i >= 0; i--){
    		sendString("close", i);
    		close(i);	
    	}
    	startServerSocket.closeServer();
    }
    
    public String pop(){
    	return startServerSocket.pop();
    }
}