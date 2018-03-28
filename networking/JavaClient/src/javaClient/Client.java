package javaClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client extends Thread {

    IOException ioException;
    UnknownHostException unknownHostException;
    String port;
    String ip;
    int choice;
    int item;
    
    Client(String ip, String port, int choice) {
        this.ip = ip;
        this.port = port;
        this.choice = choice;
    }
    
    Client(String ip, String port, int choice, int item) {
        this.ip = ip;
        this.port = port;
        this.choice = choice;
        this.item = item;
    }
	
    public void run() {

        SSLSocket socket = null;
        StringBuilder sb = new StringBuilder();
        String request = "";

        try {
        	System.out.println("creating socket...");
        	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        	socket = (SSLSocket) factory.createSocket(ip, Integer.parseInt(port));
            //socket = new Socket(ip, Integer.parseInt(port));

            //create request strings
            if(choice == 1){
            	request = "0 " + "Database~";
            }else if (choice == 2){
            	request = item + " " + "Item~";
            }else if (choice == 3){
            	request = item + " " + "Weight~";
            }
            
            // send request through socket
            System.out.println("sending request through socket...");
            System.out.println("string sent: " + request);
            OutputStream out = socket.getOutputStream();
            out.write(request.getBytes());
            out.flush();

            if(choice == 1){
            	//open file (this needs to be changed depeding on the computer right now. Need better solution)
                //File file = new File("C:\\Android\\AndriodStudioProjects\\smart-shelf\\networking\\javaClient", "database.txt");
            	//File file = new File("C:\\smart-shelf\\networking\\javaClient", "database.txt");
                File file = new File("/home/pi/smart-shelf", "database.txt");
            	
                //will need to increase size of byte array if information exceeds 1024 bytes
                byte[] bytes = new byte[1024];
                InputStream in = socket.getInputStream();
                FileOutputStream fOut = new FileOutputStream(file);
                BufferedOutputStream bOut = new BufferedOutputStream(fOut);

                //read in from the socket input stream and write to file output stream
                int bytesRead = in.read(bytes, 0, bytes.length);
                bOut.write(bytes, 0, bytesRead);
                
                //close various streams
                bOut.close();
                fOut.close();
                in.close();

                sb.append("Database received in /home/pi/smart-shelf");
            }else if (choice == 2 || choice == 3){
	            // Create byte stream to dump read bytes into
	            InputStream in = socket.getInputStream();
	
	            int byteRead = 0;
	
	            // Read from input stream. Note: inputStream.read() will block
	            // if no data return
	            System.out.println("Reading in response from socket...");
	            while (byteRead != -1) {
	                byteRead = in.read();
	                if (byteRead == 126) {
	                    byteRead = -1;
	                } else {
	                    sb.append((char) byteRead);
	                }
	            }
	            
	            //close input stream
	            in.close();
            }
            
            //finish with socket.
            socket.close();
        
        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            return;
        } catch (IOException e) {
            this.ioException = e;
            System.out.println("IOException...");
            return;
        } finally {
            if (socket != null) {
                try {
                	System.out.println("closing socket");
                    socket.close();
                } catch (IOException e) {
                    this.ioException = e;
                    System.out.println("IOException when closing socket...");
                    return;
                }
            }
        }
        System.out.println(sb.toString());
        return;
    }
}
