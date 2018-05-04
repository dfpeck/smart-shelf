package netNode;

import java.io.File;

import db.*;

public class MainServer {
    String dbName = "./inventory";
    File dbFile = new File(dbName + ".mv.db");
    Db db = null;
    
	public void main(NetServer netServer, Db db) {
		this.db = db;
		String reading = null;
		String[] readings = null;
		
		while(true){
			
			reading = netServer.pop();
			System.out.println("String popped: " + reading);
			
			if(reading != null){
				readings = reading.split(" ");
				
				if(readings[1].compareTo("Record") == 0){
					System.out.println("Id: " + readings[0] + "App wants record " + readings[1]);
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Thread sleep interrupted.");
			}
			
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
}