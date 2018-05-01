package run;

import java.util.Scanner;

import db.Db;
import netNode.NetMat;

public class MainMat{
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   final String DATABASE_FILE_NAME = "inventory";
	   NetMat netMat = null;
	   Thread netMatThread = null;
	   
	   Db db = new Db(DATABASE_FILE_NAME);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
       netMat = new NetMat(ip);
       netMatThread = new Thread(netMat);
	   netMatThread.start();
       
       while(choice != 4){
	       System.out.println("(1) Send Database, (2) Send String, (3) Strings Retrieved, (4) Exit: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netMat.sendDB(db);
	       }else if(choice == 2){ 
	    	   netMat.sendString("This is sent from TEST_NetMat.");
	       }else if (choice == 3){
	    	   System.out.println(netMat.pop());
	       }else if(choice == 4){
	    	   netMat.close();
	       }
       }
       
       /* EMULATION FUNCTIONS */
       /*
        * 1) Simulate memory at determined limit: Mat send Server DB file,
        * 										  Mat send Server string telling it to add NEW_DB it received to its DB,
        * 										  Server eventually pops this string off the stack and does the command,
        * 										  Server updates its DB file. 
        */
       scanner.close();
   }		
}