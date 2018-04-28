package netNode;

import java.sql.*;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import db.*;

public class ServerInteractor {
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

        tests.put(1, "Insert Records into Database");
        tests.put(2, "Send string to Mat");
        tests.put(3, "Send string to UI");
        tests.put(4, "Retrieve string from queue");
        
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
            	success = insertRecords();
            	break;
            case 2:
            	success = netServer.sendStringToMat("This is from the server.");
            	break;
            case 3:
            	success = netServer.sendStringToUI("This is from the server.");
            	break;
            case 4:
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
	
    public boolean insertRecords () {
        try {
            System.out.print("Inserting to ItemTypes...");
            long itemTypeId = ItemTypesRecord.insert(db, "Testing ItemType", "", false);
            System.out.println("Inserted ItemTypes record " + Long.toString(itemTypeId));

            System.out.print("Inserting to Items...");
            long itemId = ItemsRecord.insert(db, itemTypeId);
            System.out.println("Inserted Items record "+ Long.toString(itemId));

            System.out.print("Inserting to MatTypes...");
            String matTypeId = MatTypesRecord.insert(db, "DUMMYTEST",
                                                     "Dummy record for testing");
            System.out.println("inserted MatTypes record " + matTypeId);

            System.out.print("Inserting to Mats...");
            long matId = MatsRecord.insert(db, matTypeId,
                                           "Dummy record for testing");
            System.out.println("inserted Mats record " + matId);

            System.out.print("Inserting to History...");
            HistoryKey historyId = HistoryRecord.insert(db, itemId,
                                                        new Timestamp(System.currentTimeMillis()),
                                                        matId, 1,
                                                        new Double[] {1.0, 2.0},
                                                        0.0, 0.0);
            if (historyId == null) return false;
            System.out.println("inserted History record " + historyId);
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

}