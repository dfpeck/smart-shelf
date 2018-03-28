package javaServer;

import java.util.Scanner;

public class MainActivity {
	   public static void main(String[] args) {
		   Server server;
		   boolean started = false;
		   Scanner scanner = new Scanner(System.in);
		   
		   server = new Server();
		   started = true;
	       System.out.println("The server has started.");
	       System.out.println(server.getIpAddress() + ":" + server.getPort());
	       
	       scanner.nextLine();
	       scanner.close();
	       if(started == true){server.close();}
	       System.out.println("The server has stopped.");
	   }
}
