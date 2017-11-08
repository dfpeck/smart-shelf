package com.taylorhoss.matserver;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Hoss on 10/26/2017.
 * Base server code courtesty of:
 * http://androidsrc.net/android-client-server-using-sockets-server-implementation/
 */

public class Server {
    MainActivity activity;
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPort = 8080;
    private static final String TAG = "MatServer-Main";

    public Server(MainActivity activity) {
        this.activity = activity;
        SocketServerThread socketServerThread = new SocketServerThread();
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPort;
    }

    public void onDestroy() {
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
                    Log.i(TAG, "accepted socket...");
                    count++;
                    message += "#" + count + " from "
                            + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n";

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.msg.setText(message);
                        }
                    });

                    Log.i(TAG, "attempting to run request thread...");
                    SocketServerRequestThread request = new SocketServerRequestThread(socket, count);
                    request.start();

                    //SocketServerReplyThread reply = new SocketServerReplyThread(socket, count);
                    //reply.run();

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream out;
            String msgReply = "Hello from Server, you are #" + cnt + "\n"
                    + "Answer to request: Bucket of Bolts";

            try {
                // send message through socket via OutputStream
                out = hostThreadSocket.getOutputStream();
                out.write(msgReply.getBytes());

                message += "Sent: " + msgReply + "\n";

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "IOException in SocketServerReplyThread"
                        + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.msg.setText(message);
                }
            });
        }

    }

    private class SocketServerRequestThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;
        StringBuilder sb = new StringBuilder();
        private static final String TAG = "MatServer-Req";

        SocketServerRequestThread(Socket socket, int c) {
            Log.i(TAG, "socket thread constructor...");
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {

            try {
                // Create byte stream to dump read bytes into
                InputStream in = hostThreadSocket.getInputStream();

                int byteRead = 0;

                // Read from input stream. Note: inputStream.read() will block
                // if no data return
                Log.i(TAG, "attempting to read in from socket...");
                while (byteRead != -1) {
                    byteRead = in.read();
                    Log.i(TAG, "appending bytes to string...");
                    if (byteRead == 126){
                        byteRead = -1;
                    }else {
                        sb.append((char) byteRead);
                        Log.i(TAG, "done with first loop...");
                        Log.i(TAG, "string current state: " + sb.toString());
                        Log.i(TAG, "byteRead: " + (char) byteRead);
                    }
                }

                // send it back (testing)
                Log.i(TAG, "outputting response to socket...");
                OutputStream out = hostThreadSocket.getOutputStream();
                out.write((sb.toString() + "~").getBytes());
                out.flush();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.i(TAG, "IOException...");
                message += "IOException in SocketServerRequestThread"
                        + e.toString() + "\n";
            } finally {
                if (hostThreadSocket != null) {
                    try {
                        Log.i(TAG, "closing socket...");
                        hostThreadSocket.close();
                    } catch (IOException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.i(TAG, "IOException on closing socket...");
                        message += "IOException in SocketServerRequestThread"
                                + e.toString() + "\n";
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