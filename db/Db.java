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
    private ItemsRecord addWaiting = null;

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
        isOpen = false;
    }

    /** @brief For providing `port` as an int. */
    public Db (String dbName_, String hostName_, int port_, String userName_, String password_) {
        this(dbName_, hostName_, Integer.toString(port_), userName_, password_);
    }


    /* GETTERS */
    public boolean isOpen () {
        return isOpen;
    }
    
	public String getFileName() {
		return dbName;
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

        if (needsPopulation) // if the database needs tables
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
    // public void newItem (ItemsRecord item) throws SQLException {
    //     return;
    // }

    public HistoryKey updateFromSensors (Double[] sensors, int matId) throws SQLException {
        ItemsRecord item = null;
        ItemsRecord[] candidates = null;
        EventType event = null;
        Timestamp time = new Timestamp(System.currentTimeMillis());

        Double total = 0.0;
        for (Double s : sensors)
            total += s;

        if (total < 0)
            event = EventType.REMOVED;
        else
            if (addWaiting != null)
                event = EventType.ADDED;
            else
                event = EventType.REPLACED;

        switch (event) {
        case ADDED: // use the Item being added
            item = addWaiting;
            addWaiting = null;
            break;

        case REPLACED: // use the Items whose weight is nearest our sensor readings
            candidates = ItemsRecord.selectOffMat(this);
            item = candidates[0];
            for (ItemsRecord candidate : candidates)
                if (total - candidate.getWeight() < total - item.getWeight())
                    item = candidate;
            break;

        case REMOVED: // least sum of squares test
            candidates = ItemsRecord.selectOnMat(this, matId);
            Double least_sum_of_squares = Double.MAX_VALUE, candidate_sum_of_squares;
            for (ItemsRecord candidate : candidates) {
                candidate_sum_of_squares = 0.0;
                // get the sum of the squares of difference in sensor values
                for (int i=0; i < sensors.length; i++)
                    candidate_sum_of_squares +=
                        Math.pow(sensors[i]-candidate.getSensors()[i], 2);
                // if our candidate is close than our current best, replace
                if (candidate_sum_of_squares < least_sum_of_squares) {
                    least_sum_of_squares = candidate_sum_of_squares;
                    item = candidate;
                }
            }
            break;

        default:
            break;
        }

        // insert record and return key
        return HistoryRecord.insert(this, item.getId(), time, matId, event, sensors);
    }

    public HistoryKey updateFromSensors (double[] sensors, int matId) throws SQLException {
        Double[] sensorsD = new Double[sensors.length];
        for (int i=0; i<sensors.length; i++)
            sensorsD[i] = (Double) sensors[i];
        return updateFromSensors(sensorsD, matId);
    }

    /* HELPER FUNCTIONS */
    public static Vector<String> readSqlFromFile (String sqlFileName) {
        Scanner sqlScanner;
        Pattern p = Pattern.compile(".*\\S.*", Pattern.DOTALL);
            // pattern to match strings with at least one non-whitespace
            // character
        Vector<String> sqlStatements = new Vector<String>();
        //String inStr, sqlStr;

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
