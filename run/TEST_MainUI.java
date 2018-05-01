package run;

import java.util.Scanner;

import netNode.NetUI;

public class TEST_MainUI {
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
	       }
       }
       
       /* EMULATION FUNCTIONS */
       /*
        * 1) Simulate UI adding item: UI sends string to Server asking for adding an item,
        * 							  Server eventually pops this string off the stack and does the command,
        *                             Server adds new slot for that item to DB,
        *                             Server sends string to Mat telling it to add this new slot with id Server gave it,
        *                             Mat eventually pops this string off the stack and does the command,
        *                             Mat adds new slot to its DB with specific id. 
        * 2) Simulate UI requesting update: For each item in table:
        *                                                           UI sends string to Server requesting update to that record,
        *                                                           Server eventually pops this string off the stack and does the command,
        *                                                           Server sends string to UI with updated information,
        *                                                           UI eventually pops this string off the stack and does the command,
        *                                                           UI updates item. 
        */
       scanner.close();
   }		
}
   