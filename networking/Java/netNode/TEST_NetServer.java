package netNode;

import java.util.Scanner;

import netNode.NetServer;

public class TEST_NetServer {
    public void test(NetServer netServer) {
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
       
       /* choose network function */
       while(choice != 2){
    	   //check if client has closed socket or not
    	   choice = netServer.checkIfSocketClosed(choice);
    	   
    	   //prompt user for input
	       System.out.println("(1) Send String, (2) End server communication: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netServer.sendString("This is sent from TestServer");
	       }
	       
	       
       }
       scanner.close();
       return;
    }		
}
   