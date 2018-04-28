package netNode;

import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import db.*;

public class MainServer {
    String dbName = "./inventory";
    File dbFile = new File(dbName + ".mv.db");
    static HashMap<Integer, String> tests = new HashMap<Integer, String>();
    Db db = null;
    
	public void main(NetServer netServer, Db db) {
		this.db = db;
		int choice;
		int choice2;
		Boolean success = false;
        boolean loop = true;
		Scanner scanner = new Scanner(System.in);
		int count = 0;
		
        tests.put(1, "Send string to Client");
        tests.put(2, "Retrieve string from queue");
        
        while (loop) {
            System.out.println("==SELECT A TEST==");
            for (int test : tests.keySet())
                System.out.format("%2d) %s%n", test, tests.get(test));
            System.out.format("%2d) Exit\n", -1);
            choice = Integer.parseInt(scanner.nextLine());
            
            switch (choice) {
            case -1:
                loop = false;
                try {
					db.close();
					success = true;
					netServer.exit();
				} catch (SQLException e) {
					System.out.println("SQLException closing database.");
				}
                break;
            case 1:
            	System.out.println("Which socket?");
            	if(netServer.getCount() == 0){
            		System.out.println("No sockets connected yet.");
            	}else{
	            	for(count = netServer.getCount(); count > 0; count--){
	            		System.out.println(count +") " + netServer.getIdentity());
	            	}
	            	choice2 = Integer.parseInt(scanner.nextLine());
	            	if(choice2 <= netServer.getCount() && choice2 > 0){
	            		success = netServer.sendString("This is from the server.", choice2);
	            	}else{
	            		System.out.println("Not a valid socket.");
	            		success = false;
	            	}	
            	}
            	break;
            case 2:
            	System.out.println(netServer.pop());
            	success = true;
            	break;
            default:
                System.out.println("Not a correct option, try again.");
                success = false;
                break;
            }
            
            if(success)
        		System.out.println("==SUCCESS==");
        	else
        		System.out.println("==FAILED==");
        }

        System.out.println("==FINISHED==");
        scanner.close();
    }	
}