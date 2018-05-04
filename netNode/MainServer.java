package netNode;

import java.io.File;
import java.sql.SQLException;

import db.*;

public class MainServer {
    String dbName = "./inventory";
    File dbFile = new File(dbName + ".mv.db");
    Db db = null;
    
	public void main(NetServer netServer, Db db) {
		this.db = db;
		String reading = null;
		String[] readings = null;
		double[] sensors = null;
		String[] interReading = null;
		
		while(true){
			
			reading = netServer.pop();
			System.out.println("String popped: " + reading);
			
			if(reading != null){
				interReading = reading.split(" ");
				System.out.println("split[0]: " + interReading[0] + ", split[1]: " + interReading[1]);
				readings = interReading[1].split(",");
				System.out.println("split[0]: " + readings[0] + ", split[1]: " + readings[1] + "split[2]: " + readings[2] + ", split[3]: " + readings[3]);
				/*
				if(readings[1].compareTo("Record") == 0){
					System.out.println("Id: " + readings[0] + " App wants record " + readings[2]);	
				}*/
				
				sensors[0] = Double.parseDouble(readings[0]);
				sensors[2] = Double.parseDouble(readings[1]);
				sensors[3] = Double.parseDouble(readings[2]);
				sensors[4] = Double.parseDouble(readings[3]);
				
				try {
					db.updateFromSensors(sensors, Integer.parseInt(interReading[0]));
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException");
				} catch (SQLException e) {
					System.out.println("SQLException");
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