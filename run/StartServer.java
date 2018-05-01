package run;

import java.sql.SQLException;
import org.h2.tools.Server;

import db.Db;
import netNode.StartServerSocket;

public class StartServer {
	static String dbName = "./inventory";
    static String host = "localhost";
    static int port = 1066;
    
	public static void main(String[] args) {
	   /* Start H2 TCP Server and create Db objects for mat */
	   try {
		   Server tcpServer = Server.createTcpServer("-tcpPort", Integer.toString(port), "-tcpAllowOthers");
		   tcpServer.start();
		   System.out.println("The H2 TCP server has started.");
	   } catch (SQLException e) {
		   System.err.println("SQLException starting TCP Server");
	   }
	   
	   //create server's db object
	   Db db = new Db(dbName, host, port, "", "");
	   
	   try {
		   db.open();
		   System.out.println("DB Object created/opened.");
	   } catch (SQLException e) {
		   System.out.println("SQLException opening database");
	   }
	   
	   /* Start Socket Message Passing Server */
	   StartServerSocket socketServer = new StartServerSocket(db, host, port);
       System.out.println("The server has started.");
       System.out.println(socketServer.getIpAddress() + ":" + socketServer.getPort());
	}
}
