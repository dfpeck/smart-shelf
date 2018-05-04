package run;

import java.io.File;
import java.sql.SQLException;
import org.h2.tools.Server;

import db.*;
import netNode.StartServerSocket;

public class StartServer {
	static String dbName = "./inventory";
	static File dbFile = new File(dbName + ".mv.db");
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
	   dbFile.delete();
	   Db db = new Db(dbName, host, port, "", "");
	   
	   
	   try {
		   db.open();
		   
		   // Initialize Mats
		   String matTypeId = MatTypesRecord.insert(db,  "PROTO",  "Prototype Mat");
		   MatsRecord.insert(db, 0, matTypeId, "Mat 0");
		   MatsRecord.insert(db, 1,  matTypeId, "Mat 1");
		   
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
