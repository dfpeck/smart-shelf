package netNode;

import java.sql.SQLException;

import db.*;

public class MainServerThread implements Runnable{
	Db db = null;
	NetServer netServer;
	
	MainServerThread(NetServer netServer, Db db){
		this.db = db;
		this.netServer = netServer;
	}
	
	public void run(){
		//getting sensor data and inputting into database
	    while(true){
			String reading = null;
			String[] readings = null;
			double[] sensors = new double[4];
			String[] interReading = null;
			
			reading = netServer.pop();
			//System.out.println("String popped: " + reading);
			
			if(reading != null){
				interReading = reading.split(" ");
				//System.out.println("split[0]: " + interReading[0] + ", split[1]: " + interReading[1]);
				readings = interReading[1].split(",");
				//System.out.println("split[0]: " + readings[0] + ", split[1]: " + readings[1] + "split[2]: " + readings[2] + ", split[3]: " + readings[3]);
				/*
				if(readings[1].compareTo("Record") == 0){
					System.out.println("Id: " + readings[0] + " App wants record " + readings[2]);	
				}*/
				
				sensors[0] = Double.parseDouble(readings[0]);
				sensors[1] = Double.parseDouble(readings[1]);
				sensors[2] = Double.parseDouble(readings[2]);
				sensors[3] = Double.parseDouble(readings[3]);
				
				try {
					System.out.println("input into database: " + readings[0] + ", " + readings[1] + ", " + readings[2] + ", " + readings[3]);
					HistoryKey key = db.updateFromSensors(sensors, Integer.parseInt(interReading[0]));
					HistoryRecord event = HistoryRecord.selectById(db, key);
					ItemsRecord item = event.getItem();
					MatsRecord mat = event.getMat();
	
					String verb_phrase = "";
					switch (event.getEventType()) {
					case ADDED:
					    verb_phrase = " added to ";
					    break;
					case REMOVED:
					    verb_phrase = " removed from ";
					    break;
					case REPLACED:
					    verb_phrase = " placed on ";
					    break;
					}
	
					System.out.println(item + verb_phrase + mat);
					
				} catch (NumberFormatException e) {
					System.out.println("NumberFormatException");
				} catch (SQLException e) {
					System.out.println(e);
				} catch (NullPointerException e) {
					System.out.println("No Items in DB. Please use the Add"
							+ " Item feature before continuing.");
				}
			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.out.println("Thread sleep interrupted.");
			}
			
		}
	}
}
