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
		Boolean success = false;
        boolean loop = true;
		Scanner scanner = new Scanner(System.in);

        tests.put(1, "Send string to Mat");
        tests.put(2, "Send string to UI");
        tests.put(3, "Retrieve string from queue");
        
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
				} catch (SQLException e) {
					System.out.println("SQLException closing database.");
				}
                break;
            case 1:
            	success = netServer.sendStringToMat("This is from the server.");
            	break;
            case 2:
            	success = netServer.sendStringToUI("This is from the server.");
            	break;
            case 3:
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