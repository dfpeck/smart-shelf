package javaServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;


public class Server {
    ServerSocket serverSocket;
    static final int socketServerPort = 8080;

    public Server() {
        SocketServerThread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPort;
    }

    public void close() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // Creates a thread that listens on a port for incoming connects and
    // instantiates a listener thread for each of the connections requested.
    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {
                // create ServerSocket using specified port
                serverSocket = new ServerSocket(socketServerPort);

                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    Socket socket = serverSocket.accept();
                    System.out.println("accepted socket...");
                    count++;
                    System.out.println("#" + count + " from "
                    		               + socket.getInetAddress() + ":"
                    		               + socket.getPort());

                    System.out.println("attempting to run request thread...");
                    SocketServerRequestThread request = new SocketServerRequestThread(socket, count);
                    request.start();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerRequestThread extends Thread {

        private Socket hostThreadSocket;
        //int cnt;
        StringBuilder sb = new StringBuilder();

        SocketServerRequestThread(Socket socket, int c) {
        	System.out.println("socket thread constructor...");
            hostThreadSocket = socket;
            //cnt = c;
        }

        @Override
        public void run() {
            try {
                // Create byte stream to dump read bytes into
                InputStream in = hostThreadSocket.getInputStream();

                int byteRead = 0;

                // Read from input stream. Note: inputStream.read() will block
                // if no data return
                System.out.println("attempting to read in from socket...");
                while (byteRead != -1) {
                    byteRead = in.read();
                    if (byteRead == 126){
                        byteRead = -1;
                    }else {
                        sb.append((char) byteRead);
                    }
                }

                String[][] fakeDatabase = new String[][]{
                                          {"Bucket of bolts", "10lb"},
                                          {"Box of Nails", "5lb"},
                                          {"Cup of Screws", "2lb"}
                };

                //split the front and back of the string into item ID and purpose
                String[] request = sb.toString().split(" ");

                //check for errors in user input
                if(Integer.parseInt(request[0]) > fakeDatabase.length){
                    request[0] = "";
                    request[1] = "";

                    // send response
                    System.out.println("outputting response to socket...");
                    OutputStream out = hostThreadSocket.getOutputStream();
                    out.write(("Number exceeds entries in database(" + fakeDatabase.length + ").~").getBytes());
                    out.flush();

                    System.out.println("User entered number too large for database.");

                }

                // compare lexigraphically since bytes will be different
                if(request[1].compareTo("Item") == 0) {
                    // send response
                	System.out.println("outputting response to socket...");
                    OutputStream out = hostThreadSocket.getOutputStream();
                    out.write((fakeDatabase[Integer.parseInt(request[0]) - 1][0] + "~").getBytes());
                    out.flush();

                    System.out.println("Requested Item " + request[0]);

                }else if(request[1].compareTo("Weight") == 0) {
                    // send response
                	System.out.println("outputting response to socket...");
                    OutputStream out = hostThreadSocket.getOutputStream();
                    out.write((fakeDatabase[Integer.parseInt(request[0]) - 1][1] + "~").getBytes());
                    out.flush();

                    System.out.println("Requested Weight for Item " + request[0]);

                }else if(request[1].compareTo("Database") == 0){
                    // send response
                	System.out.println("outputting response to socket...");

                    //get file from external storage
                    File file = new File("C:\\smart-shelf\\networking\\testing12", "database.txt");

                    byte[] bytes = new byte[(int) file.length()];
                    BufferedInputStream bIn;

                    //read in from the file
                    bIn = new BufferedInputStream(new FileInputStream(file));
                    bIn.read(bytes, 0, bytes.length);

                    //output on socket
                    OutputStream out = hostThreadSocket.getOutputStream();
                    out.write(bytes, 0, bytes.length);
                    out.flush();
                    out.write("~".getBytes());
                    out.flush();

                    bIn.close();
                    System.out.println("Requested Database");
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("IOException in SocketServerRequestThread"
                        + e.toString());
            } finally {
                if (hostThreadSocket != null) {
                    try {
                    	System.out.println("closing socket...");
                        hostThreadSocket.close();
                    } catch (IOException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("IOException in SocketServerRequestThread"
                                + e.toString());
                    }
                }
            }
        }
    }


    // This finds the IP address that the socket is hosted on, so the server's IP/port
    // For all network interfaces and all IPs connected to said interfaces print
    // those that are site local addresses (an address which doesn't have the
    // global prefix and thus is only on this network).
    public String getIpAddress() {
        String ip = "";
        try {
            // Enumaration consisting of all interfaces on this machine
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();

            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();

                // For the specific networkInterface create an enumeration
                // consisting of all IP addresses on said interface
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();

                // Rotate through all IP addresses on the networkInterface and
                // print the IP if the IP is a SiteLocalAddress.
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "IOException in getIpAddress" + e.toString() + "\n";
        }
        return ip;
    }
}