package db;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Array;
import java.sql.Timestamp;
import java.sql.ResultSet;

import java.sql.SQLException;

/** @brief Class to represent History table entries
 */
public class HistoryRecord extends TableRecord {
    protected ItemsRecord item;
    protected Timestamp datetime;
    protected MatsRecord mat;
    protected EventType eventType;
    protected Double[] sensors;
    protected Double x, y;


    /* CONSTRUCTORS */
    public HistoryRecord (Db db_,
                          ItemsRecord item_,
                          Timestamp datetime_,
                          MatsRecord mat_,
                          EventType eventType_,
                          Double[] sensors_,
                          Double x_,
                          Double y_) {
        db = db_;
        item = item_;
        datetime = datetime_;
        mat = mat_;
        eventType = eventType_;
        sensors = sensors_;
        x = x_;
        y = y_;
    }

    public HistoryRecord (Db db_, ResultSet rs) throws SQLException {
        this(db_, ItemsRecord.selectById(db_, rs.getLong("item")), rs);
    }

    public HistoryRecord (Db db_, ResultSet rs, int row) throws SQLException {
        this(db_, getAdjustedResultSet(rs, row));
    }

    public HistoryRecord (Db db_, ItemsRecord item_, ResultSet rs) throws SQLException {
        db = db_;
        item = item_;
        datetime = rs.getTimestamp("datetime");
        mat = MatsRecord.selectById(db_, rs.getLong("mat"));
        eventType = EventType.values()[rs.getInt("eventType")];
        sensors = sqlArrayToDoubleArray(rs.getArray("sensors"));
        x = rs.getDouble("x");
        y = rs.getDouble("y");
    }

    public HistoryRecord (Db db_, ItemsRecord item_, ResultSet rs, int row) throws SQLException {
        this(db_, item_, getAdjustedResultSet(rs, row));
    }
        


    /* QUERY METHODS */
    public static HistoryRecord
        selectById (Db db_, HistoryKey key_) throws SQLException {
        // HistoryRecord's unusual key structure requires a specialized function
        PreparedStatement statement =
            db_.conn.prepareStatement("SELECT * FROM History"
                                      + " WHERE item = ? AND datetime = ?;");
        statement.setLong(1, key_.itemId());
        statement.setTimestamp(2, key_.datetime());
        return new HistoryRecord (db_, statement.executeQuery(), 1);
    }

    public static HistoryRecord
        selectById (Db db_, ItemsRecord item_, Timestamp datetime_) throws SQLException {
        return selectById(db_, new HistoryKey(item_.getId(), datetime_));
    }

    public static HistoryRecord[]
        selectByItem (Db db_, ItemsRecord item_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("SELECT * FROM History"
                                      + " WHERE item = ?"
                                      + " ORDER BY datetime DESC;",
                                      ResultSet.TYPE_SCROLL_INSENSITIVE,
                                      ResultSet.CONCUR_READ_ONLY);
        statement.setLong(1, item_.getId());
        ResultSet rs = statement.executeQuery();

        HistoryRecord[] records;
        if (rs.last()) {
            records = new HistoryRecord[rs.getRow()];
            rs.beforeFirst();
        }
        else {
            records = new HistoryRecord[0];
        }

        int row = 0;
        while (rs.next()) {
            records[row] = new HistoryRecord (db_, item_, rs);
            row++;
        }

        return records;
    }

    public static HistoryRecord[]
        selectLatestByItem (Db db_, ItemsRecord item_, int count) throws SQLException {
        HistoryRecord[] countedRecords = new HistoryRecord[count];
        HistoryRecord[] allRecords = selectByItem(db_, item_); // get all History records for `item_`

        // Return Only `count` Records //
        for (int i=0; i<count; i++)
            countedRecords[i] = allRecords[i];

        return countedRecords;
    }

    public static HistoryRecord
        selectLatestByItem (Db db_, ItemsRecord item_) throws SQLException {
        return selectLatestByItem(db_, item_, 1)[0];
    }


    /* INSERTION METHODS */
    /** @brief Insert a new record into the History table without creating an
     * object.
     *
     * This version of the method accepts the components of the History table's
     * compound primary key as separate arguments. It is generally more useful
     * for specifying newly generated History records.
     *
     * @param db_ The database into which to insert the record.
     * @param item_ `itemId` of the associated Item. Used in the primary key.
     * @param datetime_ The time associated with the record. Used in the primary
     * key.
     * @param mat_ `matId` of the mat on which the event took place.
     * @param eventType_ `eventTypeId` of the associated EventType.
     * @param sensors_ The change in sensor readings caused by the event.
     * @param x_ x-coordinate on the mat at which the//  event took place.
     * @param y_ y-coordinate of the mat at which the event took place.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns `null`.
     */
    public static HistoryKey insert (Db db_,
                                     long item_,
                                     Timestamp datetime_,
                                     long mat_,
                                     long eventType_,
                                     Double[] sensors_,
                                     Double x_,
                                     Double y_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO History"
                                     + "(item, datetime, mat, eventtype,"
                                     + " sensors, x, y)"
                                      + " VALUES (?, ?, ?, ?, ?, ?, ?);");
        statement.setLong(1, item_);
        statement.setTimestamp(2, datetime_);
        statement.setLong(3, mat_);
        statement.setLong(4, eventType_);
        statement.setArray(5, db_.conn.createArrayOf("DOUBLE", sensors_));
        statement.setDouble(6, x_);
        statement.setDouble(7, y_);
        statement.executeUpdate();
        return new HistoryKey(item_, datetime_);
    }

    /** @brief Insert a new record into the History table without creating an
     * object.
     *
     * This version of the method accepts the History table's compound primary
     * key as a single HistoryKey object. It is generally more useful for
     * copying existing history records between database instances.
     *
     * @param db_ The database into which to insert the record.
     * @param key Primary key of the History record.
     * @param mat_ `matId` of the mat on which the event took place.
     * @param eventType_ `eventTypeId` of the associated EventType.
     * @param sensors_ The change in sensor readings caused by the event.
     * @param x_ x-coordinate on the mat at which the event took place.
     * @param y_ y-coordinate of the mat at which the event took place.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns `null`.
     */
    public static HistoryKey insert (Db db_,
                                     HistoryKey key,
                                     long mat_,
                                     long eventType_,
                                     Double[] sensors_,
                                     Double x_,
                                     Double y_) throws SQLException {
        insert(db_, key.itemId(), key.datetime(), mat_, eventType_,
               sensors_, x_, y_);
        return key;
    }


    /* ACCESSORS */
    public Timestamp getDatetime () {
        return datetime;
    }

    public Double[] getSensors () {
        return sensors;
    }


    /* STANDARD METHODS */
    public String toString () {
        return "History<"
            + Long.toString(item.getId()) + ", "
            + datetime.toString() + ", "
            + eventType.name()
            + ">";
    }
}
