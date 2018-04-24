package db;

import java.sql.*;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;
import java.util.Scanner;

public class TEST_Db {
    static String prompt = "> ";
    static String testDbName = "./TEST_inventory";
    static File testDbFile = new File(testDbName + ".mv.db");
    static HashMap<Integer, String> tests = new HashMap<Integer, String>();

    public static void main (String[] args) {
        int choice;
        boolean loop = true;
		Scanner scanner = new Scanner(System.in);

        System.out.println("Testing " + testDbFile.getAbsolutePath());

        tests.put(1, "Create Database");
        tests.put(2, "Open Database");
        tests.put(3, "Read SQL from File");
        tests.put(4, "Insert Records into Database");
        // tests.put(5, "Insert Record with Manual ID");
        
        while (loop) {
            System.out.println("==SELECT A TEST==");
            for (int test : tests.keySet())
                System.out.format("%2d) %s%n", test, tests.get(test));
            System.out.format("%2d) Run all tests%n", 0);
            System.out.format("%2d) Exit\n", -1);
            choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
            case -1:
                loop = false;
                break;
            case 0:
                for (int i=1; i<=tests.size(); i++)
                    runTest(i);
                break;
            default:
                runTest(choice);
                break;
            }
        }

        System.out.println("==FINISHED==");
    }

    protected static void runTest (int test) {
        boolean success = false;

        if (tests.keySet().contains(test)) {
            System.out.println("=" + tests.get(test) + "=");

            switch (test) {
            case 1:
                success = createDatabase();
                break;
            case 2:
                success = openDatabase();
                break;
            case 3:
                success = readSql();
                break;
            case 4:
                success = insertRecords();
                break;
            }
        }
        else {
            System.out.println(test + " specifies no test");
        }

        if (success)
            System.out.println("=Success!=");
        else
            System.out.println("=Failure!=");
    }

    public static boolean openDatabase () {
        boolean success;
        Db db = new Db(testDbName);
        try {
            success = db.open();
            db.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return success;
    }

    public static boolean createDatabase () {
        if (testDbFile.exists() && !testDbFile.isDirectory()) {
            System.out.print("Removing DB file...");
            if (!testDbFile.delete()) {
                System.out.println("ERROR: Failed to delete " + testDbFile.getName());
                return false;
            }
            System.out.println(" Done!");
        }
        else {
            System.out.println(testDbFile.getAbsolutePath()
                               + " not found; no need for removal.");
        }
        return openDatabase();
    }

    public static boolean readSql () {
        Vector<String> sqlStatements
            = Db.readSqlFromFile("db/create_tables.sql");

        if (sqlStatements.size() == 0)
            return false;

        for (String sql : sqlStatements) {
            System.out.println(sql);
            System.out.println("---");
        }
        return true;
    }

    public static boolean insertRecords () {
        Db db = new Db(testDbName);
        try {
            db.open();

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

            db.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    // public static boolean insertRecordsManualId () {
    //     Db db = new Db(testDbName);
    //     long insertedId, targetId = 200;
    //     try {
    //         db.open();
    //         insertedId = ItemTypesRecord.insert(db, targetId, "Manual ID Test", "", false);
    //         System.out.println("Target ID: " + Long.toString(targetId));
    //         System.out.println("Inserted ID: " + Long.toString(insertedId));
    //         db.close();
    //     }
    //     catch (SQLException e) {
    //         System.out.println(e);
    //         return false;
    //     }
    //     return true;
    // }
}
