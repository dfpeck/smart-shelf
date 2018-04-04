package javaClient;

import java.util.Scanner;

public class MainActivity {
	   public static void main(String[] args) {
		   String port = "8080";
		   String ip = "";
		   int choice = 0;
		   int item = 0;
		   Scanner scanner = new Scanner(System.in);
		   
	       System.out.println("Input ip to connect to: ");
	       ip = scanner.nextLine();
	       
	       while(choice != 4){
		       System.out.println("(1) get database file, (2) get item, (3) get weight, (4) exit: ");
		       choice = Integer.parseInt(scanner.nextLine());
		       if(choice == 1){
		    	   Client client = new Client(ip, port, choice);
		    	   client.start();
		       }else if (choice == 2 || choice == 3){
		    	   System.out.println("Which item? ");
		    	   item = Integer.parseInt(scanner.nextLine());
		    	   Client client = new Client(ip, port, choice, item);
		    	   client.start();
		       }
	       }
	       scanner.close();
	   }		
}
