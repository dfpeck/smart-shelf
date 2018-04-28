package netNode;

import netNode.StartServerSocket;

import java.sql.SQLException;

import db.Db;

public class StartServer {
	   public static void main(String[] args) {
		   String dbName = "./inventory";
		   Db db = new Db(dbName);
		   try {
			   db.open();
		   } catch (SQLException e) {
			   System.out.println("SQLException opening database");
		   }
		   
		   StartServerSocket server = new StartServerSocket(db);
	       System.out.println("The server has started.");
	       System.out.println(server.getIpAddress() + ":" + server.getPort());
	   }
}
