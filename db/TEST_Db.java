package db;

import java.sql.*;
import org.h2.tools.Server;
import java.io.Console;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;

public class TEST_Db {
    static String prompt = "> ";
    static String testDbName = "./TEST_inventory";
    static String testHost = "localhost";
    static int testPort = 1066;
    static File testDbFile = new File(testDbName.substring(2) + ".mv.db");
    static HashMap<Integer, String> tests = new HashMap<Integer, String>();
    static long itemTypeId=1, itemId=1, matId=1;
    static String matTypeId="DUMMYTEST";
    static HistoryKey historyId = new HistoryKey(itemId, new Timestamp(System.currentTimeMillis()));
    static Server server;

    public static void main (String[] args) {
        int choice;
        boolean loop = true;
        Console console = System.console();

        try {
            server = Server.createTcpServer("-tcpPort", Integer.toString(testPort));
            server.start();

            System.out.println("Testing " + testDbFile.getAbsolutePath());

            tests.put(1, "Create Database");
            tests.put(2, "Open Database");
            tests.put(3, "Read SQL from File");
            tests.put(4, "Insert Records into Database");
            tests.put(5, "Select Records from Database");
            tests.put(6, "Select Items by Mat");

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

            server.stop();

            System.out.println("==FINISHED==");
        }
        catch (SQLException e) {
            System.err.println(e);
        }
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
                break;
            case 6:
                success = matSelections();
                break;
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
        Db db = new Db(testDbName, testHost, testPort, "", "");
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
        Db db = new Db(testDbName, testHost, testPort, "", "");
        try {
            db.open();

            System.out.print("Inserting to ItemTypes...");
            itemTypeId = ItemTypesRecord.insert(db, "On Mat 2", "This item is on a mat", false);
            ItemTypesRecord offMat = new ItemTypesRecord(db, "Off Mat", "This item is not on a mat", false);
            ItemTypesRecord onMat1 = new ItemTypesRecord(db, "On Mat 1", "This item is on mat 1", false);
            offMat.insert(); onMat1.insert();
            System.out.println("Inserted ItemTypes record " + Long.toString(itemTypeId));

            System.out.print("Inserting to Items...");
            itemId = ItemsRecord.insert(db, itemTypeId);
            ItemsRecord offMatItem = new ItemsRecord(db, offMat);
            ItemsRecord mat1item = new ItemsRecord(db, onMat1);
            offMatItem.insert(); mat1item.insert();
            System.out.println("Inserted Items record " + Long.toString(itemId));

            System.out.print("Inserting to MatTypes...");
            matTypeId = MatTypesRecord.insert(db, matTypeId, "Dummy record for testing");
            System.out.println("inserted MatTypes record " + matTypeId);

            System.out.print("Inserting to Mats...");
            matId = MatsRecord.insert(db, 1, matTypeId, "Mat 1");
            MatsRecord mat2 = new MatsRecord(db, 2, MatTypesRecord.selectById(db, matTypeId), "Mat 2");
            mat2.insert();
            System.out.println("inserted Mats record " + matId);

            System.out.print("Inserting to History...");
            historyId = HistoryRecord.insert(db, itemId,
                                             new Timestamp(System.currentTimeMillis()),
                                             matId, EventType.ADDED, new Double[] {1.0, 2.0});
            
            if (historyId == null) return false;
            HistoryRecord.insert(db, itemId, new Timestamp(System.currentTimeMillis()),
                                 matId, EventType.REMOVED, new Double[] {-1.0, -2.0});
            HistoryRecord.insert(db, itemId, new Timestamp(System.currentTimeMillis()),
                                 mat2.getId(), EventType.REPLACED, new Double[] {1.0, 2.0});

            HistoryRecord.insert(db, mat1item.getId(), new Timestamp(System.currentTimeMillis()),
                                 matId, EventType.ADDED, new Double[] {2.0, 2.0});
            
            HistoryRecord.insert(db, offMatItem.getId(), new Timestamp(System.currentTimeMillis()),
                                 mat2.getId(), EventType.ADDED, new Double[] {3.0, 2.0});
            HistoryRecord.insert(db, offMatItem.getId(), new Timestamp(System.currentTimeMillis()),
                                 mat2.getId(), EventType.REMOVED, new Double[] {-3.0, -2.0});

            System.out.println("inserted History record " + historyId);

            db.close();
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static boolean matSelections () {
        Db db = new Db(testDbName, testHost, testPort, "", "");
        try {
            db.open();

            System.out.print("Selecting items that are on mats...");
            ItemsRecord[] onMat = ItemsRecord.selectOnMat(db);
            System.out.println("Selected:");
            for (ItemsRecord itm : onMat)
                System.out.println(itm);
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    public static boolean selectRecords () {
        Db db = new Db(testDbName, testHost, testPort, "", "");
        try {
            db.open();

            System.out.print("Selecting from ItemTypes...");
            ItemTypesRecord itemType =
                ItemTypesRecord.selectById(db, itemTypeId);
            System.out.println("Selected: " + itemType.toString());

            System.out.print("Selecting from MatTypes...");
            MatTypesRecord matType =
                MatTypesRecord.selectById(db, matTypeId);
            System.out.println("Selected: " + matType.toString());

            System.out.print("Selecting from Mats...");
            MatsRecord mat =
                MatsRecord.selectById(db, matId);
            System.out.println("Selected: " + mat.toString());

            System.out.print("Selecting from Items...");
            ItemsRecord item =
                ItemsRecord.selectById(db, itemId);
            System.out.println("Selected: " + item.toString());

            System.out.print("Selecting from History...");
            HistoryRecord history =
                HistoryRecord.selectLatestByItem(db, item);
            System.out.println("Selected: " + history.toString());

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
