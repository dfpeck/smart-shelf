package db;

import java.sql.*;
import java.io.File;
import java.util.Scanner;
import java.util.regex.*;
import java.util.Vector;

import java.io.FileNotFoundException;

/**
 * Main driver for the database.
 */
public class Db {
    /* PROPERTIES */
    /** The name of the database. Do not include ".mv.db" extension.
     */
    Connection conn;
    private String name;
    private File file;
    private boolean isOpen;

    /* CONSTRUCTORS */
    public Db (String name_init) {
        name = name_init;
        file = new File(name + ".mv.db");
        conn = null;
    }

    /* GETTERS */
    public boolean isOpen () {
        return isOpen;
    }

    /* INITIALIZATION METHODS */
    /** @brief Create a new database.
     *
     * @return Success or failure.
     */
    protected boolean create () {
        try {
        for (String sql : readSqlFromFile("db/create_tables.sql"))
            conn.prepareStatement(sql).execute();
        }
        catch (SQLException e) {
            System.err.println(e);
            return false;
        }
        return true;
    }

    /** @brief Open the database connection.
     *
     * Must be called before database is usable.
     */
    public boolean open () {
        boolean needsPopulation = !(file.exists() && !file.isDirectory());

        try {
            Class.forName("org.h2.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
            return false;
        }

        try {
            conn = DriverManager.getConnection("jdbc:h2:" + name);
        }
        catch (SQLException e) {
            System.out.println(e);
            return false;
        }

        if (needsPopulation) // if the database needs tables
            if (!create()) // populate it, and on failure…
                return false;

        isOpen = true;
        return true;
    }

    /** @brief Close the database connection.
     */
    public void close () throws SQLException {
        conn.close();
        isOpen = false;
    }

    /* QUERY METHODS */

    /* HELPER FUNCTIONS */
    public static Vector<String> readSqlFromFile (String sqlFileName) {
        Scanner sqlScanner;
        Pattern p = Pattern.compile(".*\\S.*", Pattern.DOTALL);
            // pattern to match strings with at least one non-whitespace
            // character
        Vector<String> sqlStatements = new Vector<String>();
        String inStr, sqlStr;

        try {
            sqlScanner = new Scanner(new File(sqlFileName));
            sqlScanner.useDelimiter(";");

            while (sqlScanner.hasNext(p))
                sqlStatements.add(sqlScanner.next(p) + ";");

            sqlScanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }

        return sqlStatements;
    }
}
