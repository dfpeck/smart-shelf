package run;

import java.util.Scanner;

import netNode.NetUI;

public class MainUI {
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   NetUI netUI = null;
	   Thread netUIThread = null;
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
     
       netUI = new NetUI(ip);
       netUIThread = new Thread(netUI);
	   netUIThread.start();
       
       while(choice != 3){
	       System.out.println("(1) Send String, (2) Strings Retrieved, (3) End server communication: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netUI.sendString("This is sent from TEST_NetUI.");
	       }else if(choice == 2){
	    	   System.out.println(netUI.pop());
	       }else if(choice == 3){
	    	   netUI.close();
	    	   if (netUIThread != null){
	    		   System.out.println("ending netUIThread");
	    		   try {
	    			   netUIThread.join();
	    		   } catch (InterruptedException e) {
	    			   System.out.println("netUIThread.join interrupted");
	    		   }
	    		   System.out.println("ended netUIThread.");
	    	   }else{
	    		   System.out.println("netMatThread == null");
	    	   }
	       }
       }
       scanner.close();
   }		
}
   