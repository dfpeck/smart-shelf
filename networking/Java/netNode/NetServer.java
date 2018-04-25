package netNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Test.TEST_NetServer;


public class NetServer extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    Socket socket = null;
    InputStream in = null;
    OutputStream out = null;

    public NetServer(Socket s) {
        socket = s;
        
        try {
        	System.out.println("getting input and output streams for NetServer...");
            in = socket.getInputStream();
            out = socket.getOutputStream();

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
	
    public void run() {
		//starts loop for user input in new thead. Not needed in final implementation, as they'll
		//be calling the functions of their own accord.
    	TEST_NetServer testServer = new TEST_NetServer();
    	testServer.test(this);
    	System.out.println("NetServer ready for function calls...");
    }
    
    public void sendString(String str){
    	//create request string
        String intent = "SendString~";
        
        try {
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
			System.out.println("IOException in request()");
        }
    }

    public int checkIfSocketClosed(int choice){
    	if(socket == null){
    		System.out.println("Client closed socket...ending communication.");
    		return 3;
    	}
		return choice;
    }
}

