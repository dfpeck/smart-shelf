package netNode;

import java.io.File;
import java.util.Scanner;

import netNode.NetMat;
import netNode.Db;

public class TEST_NetMat{
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   final String DATABASE_FILE_NAME = "TEST_inventory.mv.db";
	   
	   File file = new File(DATABASE_FILE_NAME);
	   Db db = new Db();
	   db.setFile(file);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
       NetMat netMat = new NetMat(ip);
	   netMat.start();
       
       while(choice != 4){
	       System.out.println("(1) Send Database, (2) Send String, (3) Strings Retrieved, (4) Exit: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netMat.sendDB(db);
	       }else if(choice == 2){ 
	    	   netMat.sendString("This is sent from TEST_NetMat.");
	    	   netMat.sendString("This is also from TEST_NetMat.");
	    	   netMat.sendString("Same.");
	       }else if (choice == 3){
	    	   System.out.println(netMat.pop());
	    	   System.out.println(netMat.pop());
	    	   System.out.println(netMat.pop());
	       }else if(choice == 4){
	    	   netMat.close();
	       }
       }
       scanner.close();
   }		
}