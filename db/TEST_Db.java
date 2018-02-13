package db;

import java.sql.*;
import java.io.Console;
import java.io.File;
import java.util.Vector;
import java.util.HashMap;

public class TEST_Db {
    static String prompt = "> ";
    static String testDbName = "./TEST_inventory";
    static File testDbFile = new File(testDbName + ".mv.db");
    static HashMap<Integer, String> tests = new HashMap<Integer, String>();

    public static void main (String[] args) {
        int choice;
        boolean loop = true;
        Console console = System.console();

        System.out.println("Testing " + testDbFile.getAbsolutePath());

        tests.put(1, "Create Database");
        tests.put(2, "Open Database");
        tests.put(3, "Read SQL from File");
        tests.put(4, "Insert Records into Database");
        
        while (loop) {
            System.out.println("==SELECT A TEST==");
            for (int test : tests.keySet())
                System.out.format("%2d) %s%n", test, tests.get(test));
            System.out.format("%2d) Run all tests%n", 0);
            System.out.format("%2d) Exit\n", -1);
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

        System.out.println("==FINISHED==");
    }

    protected static void runTest (int test) {
        boolean success = false;

        if (tests.keySet().contains(test)) {
            System.out.println("=" + tests.get(test) + "=");

            switch (test) {
            case 2:
                success = openDatabase();
                break;
            case 3:
                success = readSql();
                break;
            case 1:
                success = createDatabase();
                break;
            case 4:
                success = insertRecords();
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
        Db db = new Db(testDbName);
        boolean success = db.open();
        try {db.close();} catch (SQLException e) {}
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
        db.open();
        try {
            long id = ItemTypesRecord.insert(db, "Testing ItemType", false);
            System.out.println("Inserted ItemTypes record " + Long.toString(id));
            db.close();
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
