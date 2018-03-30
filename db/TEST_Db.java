package db;

import java.sql.*;
import java.io.Console;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;

public class TEST_Db {
    static String prompt = "> ";
    static String testDbName = "./TEST_inventory";
    static File testDbFile = new File(testDbName.substring(2) + ".mv.db");
    static HashMap<Integer, String> tests = new HashMap<Integer, String>();
    static long itemTypeId=1, itemId=1, matId=1;
    static String matTypeId="DUMMYTEST";
    static HistoryKey historyId;

    public static void main (String[] args) {
        int choice;
        boolean loop = true;
        Console console = System.console();

        System.out.println("Testing " + testDbFile.getAbsolutePath());

        tests.put(1, "Create Database");
        tests.put(2, "Open Database");
        tests.put(3, "Read SQL from File");
        tests.put(4, "Insert Records into Database");
        tests.put(5, "Select Records from Database");

        while (loop) {
            System.out.println("==SELECT A TEST==");
            for (int test : tests.keySet())
                System.out.format("%2d) %s%n", test, tests.get(test));
            System.out.format("%2d) Run all tests%n", 0);
            System.out.format("%2d) Exit\n", -1);
            try {
                choice = Integer.parseInt(console.readLine(prompt));

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
            catch (NumberFormatException e) {
                System.out.println("Please enter a number");
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
            case 5:
                success = selectRecords();
            }
        }
        else {
            System.out.println(test + " specifies no test");
            return;
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
            itemTypeId = ItemTypesRecord.insert(db, "Testing ItemType", "", false);
            System.out.println("Inserted ItemTypes record " + Long.toString(itemTypeId));

            System.out.print("Inserting to Items...");
            itemId = ItemsRecord.insert(db, itemTypeId);
            System.out.println("Inserted Items record "+ Long.toString(itemId));

            System.out.print("Inserting to MatTypes...");
            matTypeId = MatTypesRecord.insert(db, matTypeId, "Dummy record for testing");
            System.out.println("inserted MatTypes record " + matTypeId);

            System.out.print("Inserting to Mats...");
            matId = MatsRecord.insert(db, matTypeId, "Dummy record for testing");
            System.out.println("inserted Mats record " + matId);

            System.out.print("Inserting to History...");
            historyId = HistoryRecord.insert(db, itemId,
                                             new Timestamp(System.currentTimeMillis()),
                                             matId, 1, new Double[] {1.0, 2.0},
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

    public static boolean selectRecords () {
        Db db = new Db(testDbName);
        try {
            db.open();

            // System.out.print("Selecting from ItemTypes...");
            // ItemTypesRecord itemType =
            //     ItemTypesRecord.selectById(db, itemTypeId);
            // System.out.println("Selected: " + itemType.toString());

            // System.out.print("Selecting from MatTypes...");
            // MatTypesRecord matType =
            //     MatTypesRecord.selectById(db, matTypeId);
            // System.out.println("Selected: " + matType.toString());

            // System.out.print("Selecting from Mats...");
            // MatsRecord mat =
            //     MatsRecord.selectById(db, matId);
            // System.out.println("Selected: " + mat.toString());

            System.out.print("Selecting from Items...");
            ItemsRecord item =
                ItemsRecord.selectById(db, itemId);
            System.out.println("Selected: " + item.toString());

            db.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    // NOT USED IN CURRENT IMPLEMENTATION
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
