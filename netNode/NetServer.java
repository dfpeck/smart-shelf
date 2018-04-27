package netNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import netNode.TEST_NetServer;
import netNode.StartServer;


public class NetServer extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    Socket matSocket = null;
    InputStream matIn = null;
    OutputStream matOut = null;
    Socket uiSocket = null;
    InputStream uiIn = null;
    OutputStream uiOut = null;
    StartServer startServer = null;

    public NetServer(StartServer startServer) {
        this.startServer = startServer;
    }
	
    public void run() {
		//starts loop for user input in new thead. Not needed in final implementation, as they'll
		//be calling the functions of their own accord.
    	TEST_NetServer testServer = new TEST_NetServer();
    	testServer.test(this);
    	System.out.println("NetServer ready for function calls...");
    }
    
    public void setSocket(Socket socket, String client){
		System.out.println("comparison time...");
		if(client.compareTo("mat") == 0){
            try {
            	System.out.println("getting input and output streams for NetServer...");
            	matSocket = socket;
                matIn = socket.getInputStream();
                matOut = socket.getOutputStream();

            } catch (UnknownHostException e) {
                this.unknownHostException = e;
                System.out.println("UnknownHostException in socket creation");
                return;
            } catch (IOException e) {
                this.ioException = e;
                System.out.println("IOException in socket creation");
                return;
            }
    	}else if(client.compareTo("ui") == 0){
            try {
            	System.out.println("getting input and output streams for NetServer...");
            	uiSocket = socket;
                uiIn = socket.getInputStream();
                uiOut = socket.getOutputStream();

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
    }
    
    public void sendStringToMat(String str){
    	//create request string
        String intent = "SendString~";
        
        try {
        	matOut.flush();
        	matOut.write(intent.getBytes());
        	matOut.flush();
	        System.out.println("sendString intent sent...");
	        
	        //send string
	        matOut.flush();
	        matOut.write(str.getBytes());
	        matOut.flush();
	        matOut.write("~".getBytes());
	        matOut.flush();
	        System.out.println("string sent: " + str);
	       
        } catch (IOException e){
			e.printStackTrace();
			System.out.println("IOException in request()");
        }
    }
    
    public void sendStringToUI(String str){
    	//create request string
        String intent = "SendString~";
        
        try {
        	uiOut.flush();
        	uiOut.write(intent.getBytes());
        	uiOut.flush();
	        System.out.println("sendString intent sent...");
	        
	        //send string
	        uiOut.flush();
	        uiOut.write(str.getBytes());
	        uiOut.flush();
	        uiOut.write("~".getBytes());
	        uiOut.flush();
	        System.out.println("string sent: " + str);
	       
        } catch (IOException e){
			e.printStackTrace();
			System.out.println("IOException in request()");
        }
    }

    public int checkIfSocketClosed(int choice){
    	if(matSocket == null && uiSocket == null){
    		System.out.println("Mat && UI Client closed socket...ending communication.");
    		return 5;
    	}else if(uiSocket == null){
    		System.out.println("UI Client closed socket...ending communication.");
    		return 4;
    	}else if(matSocket == null){
    		System.out.println("UI Client closed socket...ending communication.");
    		return 3;
    	}
		return choice;
    }
    
    public String pop(){
    	return startServer.pop();
    }
}