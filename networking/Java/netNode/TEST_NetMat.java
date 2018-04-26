package netNode;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import netNode.NetMat;

public class TEST_NetMat{
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   
	   //setup database object
	   Db db = new Db();
	   URL url = TEST_NetMat.class.getResource("SendDatabase.db");
	   File file = new File(url.getPath());
	   db.setFile(file);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
       //initialize socket in new thread
       NetMat netMat = new NetMat(ip);
	   netMat.start();
       
       /* choose network function */
       while(choice != 3){
	       System.out.println("(1) Send Database, (2) Send String, (3) Strings Retrieved, (4) Exit: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   netMat.sendDB(db);
	       }else if(choice == 2){ 
	    	   netMat.sendString("This is sent from TestServer.");
	    	   netMat.sendString("This is also from Test Server.");
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
   
