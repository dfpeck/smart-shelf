package netNode;

import java.util.Scanner;

import netNode.NetServer;

public class TEST_NetServer {
    public void test(NetServer netServer) {
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
       
       /* choose network function */
       while(choice != 3){
    	   //check if client has closed socket or not
    	   choice = netServer.checkIfSocketClosed(choice);
    	   
	       System.out.println("(1) Send String, (2) Strings Retrieved, (3) End server communication: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netServer.sendString("This is sent from TestServer.");
	    	   netServer.sendString("This is also from Test Server.");
	    	   netServer.sendString("Same.");
	       }else if(choice == 2){
	    	   System.out.println(netServer.pop());
	    	   System.out.println(netServer.pop());
	    	   System.out.println(netServer.pop());
	       }else if(choice == 3){
	    	   
	       }
       }
       scanner.close();
       return;
    }		
}
   