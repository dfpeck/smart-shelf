package run;

import java.util.Scanner;

import netNode.NetUI;

public class TEST_MainUI {
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   int choice2 = 0;
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
	       System.out.println("(1) Ask for ItemRecord, (2) send mat data, (3) End server communication: ");
	       try{
	    	   choice = Integer.parseInt(scanner.nextLine());
	       } catch(NumberFormatException e){
	    	   System.out.println("Not an number, try again.");
	       }
	       switch(choice){
	       case 1:
	    	   try{
	    		   System.out.println("Enter a record number");
	    		   choice2 = Integer.parseInt(scanner.nextLine());
	    	   } catch(NumberFormatException e){
		    	   System.out.println("Not an number.");
		       }
	    	   netUI.sendString("Record " + choice2);
	    	   break;
	       case 2:
	    	   netUI.sendString("0 1.25,0.35,4.56,0.23");
	    	   break;
	       case 3:
	    	   netUI.close();
	    	   break;
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
   