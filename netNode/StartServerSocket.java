package netNode;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import db.Db;

/** @brief Starts the components of the server*/
public class StartServerSocket {
	/*PROPERTIES*/
	static String host = "";
    static int tcpServerPort = 0;
    
	NetServer netServer = null;
	Thread netServerThread = null;
	SocketServer socketServer = null;
	Thread socketServerThread = null;
	Db db = null;

	/*CONSTRUCTORS*/
	/**
	 * 
	 * @param db the db object
	 * @param host the host name of the server
	 * @param tcpServerPort the port of the tcp server
	 */
    public StartServerSocket(Db db, String host, int tcpServerPort) {
    	this.db = db;
    	StartServerSocket.host = host;
    	StartServerSocket.tcpServerPort = tcpServerPort;
    	
    	netServer = new NetServer(this, db);
    	netServerThread = new Thread(netServer);
        netServerThread.start();
    	
    	socketServer = new SocketServer(netServer, db, host, tcpServerPort);
    	socketServerThread = new Thread(socketServer);
    	socketServerThread.start();  
    	
    	netServer.setSocketServer(socketServer);
    }
    
    /*GETTERS*/
    public String getPort(){
    	return Integer.toString(socketServer.getPort());
    }
    
    /** @brief getsIPAddress of server
     * 
     * This finds the IP address that the socket is hosted on, so the server's IP/port
     * For all network interfaces and all IPs connected to said interfaces print
     * those that are site local addresses (an address which doesn't have the
     * global prefix and thus is only on this network).
     * 
     * */
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
            ip += "IOException in getIpAddress" + e.toString() + "\n";
        }
        return ip;
    }  
}

