package netNode;

import netNode.StartServerSocket;

import java.sql.SQLException;
import org.h2.tools.Server;

import db.Db;

public class StartServer {
	public static void main(String[] args) {
	   /* Start H2 TCP Server and create Db objects for mat */
	   //try {
		   //Server server = Server.createTcpServer("-tcpPort", "8080", "-tcpAllowOthers").start();
		   System.out.println("The H2 TCP server has started.");
	   //} catch (SQLException e) {
	   //	System.err.println("SQLException starting TCP Server");
	   //}
	   
	   /* Create Db objects */
	   // create Db object that's connected to this this server
	   // create Db objects that are connected to each mat
	   
	   //Here I'm creating a dummy db object so my code functions.
	   String dbName = "./inventory";
	   Db db = new Db(dbName);
	   try {
		   db.open();
		   System.out.println("DB Object created.");
	   } catch (SQLException e) {
		   System.out.println("SQLException opening database");
	   }
	   
	   /* Start Socket Message Passing Server */
	   StartServerSocket server = new StartServerSocket(db);
       System.out.println("The server has started.");
       System.out.println(server.getIpAddress() + ":" + server.getPort());
	}
}
