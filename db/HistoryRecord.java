package db;

import java.sql.PreparedStatement;
import java.sql.Array;
import java.sql.Timestamp;

import java.sql.SQLException;

/** @brief Class to represent History table entries
 */
public class HistoryRecord extends TableRecord {
    protected ItemsRecord item;
    protected Timestamp datetime;
    protected MatsRecord mat;
    protected EventTypesRecord eventType;
    protected Double[] sensors;
    protected Double x, y;


    /* CONSTRUCTORS */
    public HistoryRecord (Db db_,
                          ItemsRecord item_,
                          Timestame datetime_,
                          MatsRecord mat_,
                          EventTypesRecord eventType_,
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
     * @param x_ x-coordinate on the mat at which the event took place.
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
        insert(db_, key.getItem(), key.getDatetime(), mat_, eventType_,
               sensors_, x_, y_);
        return key;
    }
}
                                     
