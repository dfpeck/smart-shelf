package Test;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import netNode.NetMat;

public class TestMat{
   public static void main(String[] args) {
	   String ip = "";
	   int choice = 0;
	   Scanner scanner = new Scanner(System.in);
	   
	   /* need to manually input ip until ip scanner function is created */
       System.out.println("Input ip to connect to: ");
       ip = scanner.nextLine();
       
       //initialize socket in new thread
       NetMat netMat = new NetMat(ip);
	   netMat.start();
       
       /* choose network function */
       while(choice != 3){
	       System.out.println("(1) Send Database, (2) Send String, (3) Exit: ");
	       choice = Integer.parseInt(scanner.nextLine());
	       if(choice == 1){
	    	   //get file from external storage
	    	   URL url = TestMat.class.getResource("databaseToSend.txt");
	           File file = new File(url.getPath());
	           
	    	   netMat.sendDB(file);
	       }else if(choice == 2){ 
	    	   netMat.sendString("This is from TestMat");
	       }else if(choice == 3){
	    	   netMat.close();
	       }
       }
       scanner.close();
   }		
}
   