package netNode;

import java.util.Scanner;

import netNode.NetServer;

public class TEST_NetServer {
    public void test(NetServer netServer) {
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
       
       /* choose network function */
       while(choice != 6){
    	   //check if client has closed socket or not
    	   choice = netServer.checkIfSocketClosed(choice);
    	   
	       System.out.println("(1) Send String to mat\n(2) Send String to UI\n(3) Strings Retrieved\n(4) Close socket with UI\n(5) Close socket with Mat\n(6) Close all sockets: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netServer.sendStringToMat("This is sent from TestServer.");
	    	   netServer.sendStringToMat("This is also from TestServer.");
	    	   netServer.sendStringToMat("Same.");
	       }else if(choice == 2){
	    	   netServer.sendStringToUI("This is sent from TestServer.");
	    	   netServer.sendStringToUI("This is also from TestServer.");
	    	   netServer.sendStringToUI("Same.");
	       }else if(choice == 3){
	    	   System.out.println(netServer.pop());
	    	   System.out.println(netServer.pop());
	    	   System.out.println(netServer.pop());
	       }else if(choice == 4){
	    	   // TODO close socket with UI
	       }else if(choice == 5){
	    	   // TODO close socket with mat
	       }else if(choice == 6){
	    	   // TODO close both sockets
	       }
       }
       scanner.close();
       return;
    }		
}
   