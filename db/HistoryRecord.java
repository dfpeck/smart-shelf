package db;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Array;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

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
                          Double[] sensors_) {
        db = db_;
        item = item_;
        datetime = datetime_;
        mat = mat_;
        eventType = eventType_;
        sensors = sensors_;
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
    }

    public HistoryRecord (Db db_, ItemsRecord item_, ResultSet rs, int row) throws SQLException {
        this(db_, item_, getAdjustedResultSet(rs, row));
    }
        


    /* QUERY METHODS */
    /** @brief Select a History record using its ID (primary key value).
     *
     * @param db_ Db object connected to the database to be queried
     * @param key_ ID of the History record
     *
     * @return HistoryRecord representing the selected record
     */
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

    /** @brief Select a History record using its ID (primary key value).
     *
     * @param db_ Db object connected to the database to be queried
     * @param item_ Items record associated with the History record
     * @param datetime_ java.sql.Timestamp associated with the History record
     *
     * @return HistoryRecord representing the selected record
     */
    public static HistoryRecord
        selectById (Db db_, ItemsRecord item_, Timestamp datetime_) throws SQLException {
        return selectById(db_, new HistoryKey(item_.getId(), datetime_));
    }

    /** @brief Select the single most recent History record associated with a
     * particular item.
     *
     * @param db_ Db object connected to the database to be queried
     * @param item_ Items record to query the history of
     *
     * @param HistoryRecord representing the selected record
     */
    public static HistoryRecord
        selectLatestByItem (Db db_, ItemsRecord item_) throws SQLException {
        return selectLatestByItem(db_, item_, 1)[0];
    }

    /** @brief Select the `count` most recent History records associated with a
     * particular item.
     *
     * @param db_ Db object connected to the database to be queried
     * @param item_ Items record to query the history of
     * @param count number of records to return
     *
     * @return list of HistoryRecords representing the selected records
     */
    public static HistoryRecord[]
        selectLatestByItem (Db db_, ItemsRecord item_, int count) throws SQLException {
        HistoryRecord[] countedRecords = new HistoryRecord[count];
        HistoryRecord[] allRecords = selectAllByItem(db_, item_); // get all History records for `item_`

        // Return Only `count` Records //
        for (int i=0; i<count; i++)
            countedRecords[i] = allRecords[i];

        return countedRecords;
    }

    /** @brief Select all History records associated with a particular item.
     *
     * @param db_ Db object connected to the database to be queried
     * @param item_ Items record to query the history of
     *
     * @return list of HistoryRecords representing the selected
     * records in reverse chronological order
     */
    public static HistoryRecord[]
        selectAllByItem (Db db_, ItemsRecord item_) throws SQLException {
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
        while (rs.next())
            records[row++] = new HistoryRecord(db_, item_, rs);

        return records;
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
                                     EventType eventType_,
                                     Double[] sensors_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO History"
                                     + "(item, datetime, mat, eventtype,"
                                     + " sensors)"
                                      + " VALUES (?, ?, ?, ?, ?);");
        statement.setLong(1, item_);
        statement.setTimestamp(2, datetime_);
        statement.setLong(3, mat_);
        statement.setLong(4, eventType_.ordinal());
        statement.setArray(5, db_.conn.createArrayOf("DOUBLE", sensors_));
        // statement.setDouble(6, x_);
        // statement.setDouble(7, y_);
        try {TimeUnit.MILLISECONDS.sleep(1);} // pause to ensure unique primary keys
        catch (InterruptedException e) {}
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
                                     EventType eventType_,
                                     Double[] sensors_) throws SQLException {
        insert(db_, key.itemId(), key.datetime(), mat_, eventType_, sensors_);
        return key;
    }


    /* ACCESSORS */
    /** @brief The item associated with the event. */
    public ItemsRecord getItem () {
        return item;
    }

    /** @brief The time at which the event occurred. */
    public Timestamp getDatetime () {
        return datetime;
    }

    /** @brief Whether the associated item was on a mat or not after the event. */
    public boolean isOnMat () {
        return eventType != EventType.REMOVED;
            // || eventType == EventType.DISABLED);
    }

    /** @brief The mat associated with the event. */
    public MatsRecord getMat () {
        return mat;
    }
    
    /** @brief The type of event that occurred. */
    public EventType getEventType () {
        return eventType;
    }

    /** @brief The change in sensor readings the event produced. */
    public Double[] getSensors () {
        return sensors;
    }

    /** @brief The weight of the associated item after the event. */
    public Double getWeight () {
        Double weight = 0.0;
        for (Double d : sensors)
            weight += d;
        return Math.abs(weight);
    }

    // /** @brief The x-coordinate of the associated item on the associated mat
    //  * after the event. */
    // public Double getX () {
    //     return x;
    // }

    // /** @brief The y-coordinate of the associated item on the associated mat
    //  * after the event. */
    // public Double getY () {
    //     return y;
    // }

    // /** @brief The coordinates of the associated item on the associated mat
    //  * after the event. */
    // public Double[] getCoords () {
    //     return new Double[]{x, y};
    // }


    /* STANDARD METHODS */
    public String toString () {
        return "History<"
            + Long.toString(item.getId()) + ", "
            + datetime.toString() + ", "
            + eventType.name()
            + ">";
    }
}
