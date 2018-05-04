package netNode;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

import db.*;

public class MainServer {
    String dbName = "./inventory";
    File dbFile = new File(dbName + ".mv.db");
    Db db = null;
    int choice, itemNum = 0;
	HashMap<Integer, String> options = new HashMap<Integer, String>();
	MainServerThread mainServerThread = null;
    Thread mainServerThreadThread = null;
	public void main(NetServer netServer, Db db) {
		this.db = db;
		boolean loop = true;
		Scanner scanner = new Scanner(System.in);
		
		mainServerThread = new MainServerThread(netServer, db);
		mainServerThreadThread = new Thread(mainServerThread);
		mainServerThreadThread.start();
		
		// setup code
		options.put(1, "Prepare mat for adding a new item");
		options.put(0, "Exit");
		
		while(loop){
			System.out.println("==SELECT AN OPTION==");
			for (int opt : options.keySet())
			    System.out.format("%2d) %s%n", opt, options.get(opt));

			try {
				choice = Integer.parseInt(scanner.nextLine());

			    switch(choice) {
			    case 0:
			        // code to end loop
			    	loop = false;
			        break;

			    case 1:
			        ItemTypesRecord itemType = new ItemTypesRecord(db, "Item " + Integer.toString(itemNum++), "", false);
			        itemType.insert();
			        db.newItem(new ItemsRecord(db, itemType));
			        break;
			    }
			} catch (NumberFormatException e) {
			    System.out.println("Please enter a number");
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		scanner.close();
	}
        /* EMULATION FUNCTIONS */
        /*
         * 1) Simulate request DB from mat: Server sends string to Mat asking for DB update,
         * 									Mat eventually pops this string off the stack and does the command,
         *                                  Mat sends DB file to Server,
         *                                  Mat sends string to Server telling it to update its database,
         *                                  Server eventually pops this string off the stack and does the command,
         *                                  Server updates its DB with NEW_DB file.
         *    When will the above function happen?
         *    1) Every so much time.
         *    2) First time UI requests a update to a record in a certain amount of time.
         */	
}