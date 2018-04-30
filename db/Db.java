package db;

import java.sql.*;
import java.io.File;
import java.util.Scanner;
import java.util.regex.*;
import java.util.Vector;

import java.io.FileNotFoundException;

/**
 * @brief Main driver for the database.
 */
public class Db {
    /* PROPERTIES */
    /** The name of the database. Do not include ".mv.db" extension.
     */
    Connection conn;
    private String dbName;
    private String hostName;
    private String port;
    private String userName, password;
    private File file;
    private boolean isOpen;

    /* CONSTRUCTORS */
    /** 
     * @param dbName_ the name of the database, including the file path (from
     * root), but not including the extension, which is managed by H2
     * @param hostName_ the name of the host on the network
     * @param port_ the port on which the server is broadcasting
     * @param userName the user with which to access the server
     * @param password the user's password
     */
    public Db (String dbName_, String hostName_, String port_, String userName_, String password_) {
        dbName = dbName_;
        hostName = hostName_;
        port = port_;
        userName = userName_;
        password = password_;
        file = new File(dbName + ".mv.db");
        conn = null;
    }

    public Db (String dbName_, String hostName_, int port_, String userName_, String password_) {
        this(dbName_, hostName_, Integer.toString(port_), userName_, password_);
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
    protected void create () throws SQLException {
        for (String sql : readSqlFromFile("db/create_tables.sql"))
            conn.prepareStatement(sql).execute();
        for (EventType e : EventType.values())
            e.insert(this);
    }

    /** @brief Open the database connection.
     *
     * Must be called before database is usable.
     */
    public boolean open () throws SQLException {
        boolean needsPopulation = !file.exists() || file.isDirectory();

        try {
            Class.forName("org.h2.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
            return false;
        }

        conn = DriverManager.getConnection("jdbc:h2:tcp://" + hostName + ":" + port
                                           + "/" + dbName, userName, password);

        if (needsPopulation) // if the database needs tables…
            create();        // populate it

        isOpen = true;
        return true;
    }

    /** Close the database connection.
     */
    public void close () throws SQLException {
        conn.close();
        isOpen = false;
    }

    /* UPDATE METHODS */

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
