package run;

import java.util.Scanner;

import db.Db;
import netNode.NetMat;

public class MainMat{
	static String dbName = "./inventory";
    static int tcpPort = 1066;
	
   public static void main(String[] args) {
	   
	   String ip = "";
	   int choice = 0;
	   NetMat netMat = null;
	   Thread netMatThread = null;
	   
	   Scanner scanner = new Scanner(System.in);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
	   //create clients db object
	   Db db = new Db(dbName, ip, tcpPort, "", "");
       
       netMat = new NetMat(ip);
       netMatThread = new Thread(netMat);
	   netMatThread.start();
       
       while(choice != 4){
	       System.out.println("(1) Send Database, (2) Send String, (3) Strings Retrieved, (4) Exit: ");
	       try{
	    	   choice = Integer.parseInt(scanner.nextLine());
	       } catch(NumberFormatException e){
	    	   System.out.println("Not an number, try again.");
	       }
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