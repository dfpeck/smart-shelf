package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.sql.SQLException;

/** @brief Class to represent Items table entries
 */
public class ItemsRecord extends TableRecord {
    protected long itemId;
    protected ItemTypesRecord itemType;
    protected HistoryRecord lastHistory;


    /* CONSTRUCTORS */
    public ItemsRecord (Db db_,
                        long itemId_,
                        ItemTypesRecord itemType_,
                        HistoryRecord lastHistory_) {
        db = db_;
        itemId = itemId_;
        itemType = itemType_;
        lastHistory = lastHistory_;
    }

    public ItemsRecord (Db db_, ResultSet rs) throws SQLException {
        db = db_;
        itemId = rs.getLong("itemId");
        itemType = ItemTypesRecord.selectById(db_, rs.getLong("itemType"));
        lastHistory = HistoryRecord.selectLatestByItem(db, this);
    }

    public ItemsRecord (Db db_, ResultSet rs, int row) throws SQLException {
        this(db_, getAdjustedResultSet(rs, row));
    }


    /* QUERY METHODS */
    public static ItemsRecord selectById (Db db_, long itemId_)
        throws SQLException {
        return new ItemsRecord(db_, selectByIdLong(db_, itemId_, "Items", "itemId"));
    }


    /* INSERTION METHODS */
    /** @brief Insert a new record into the Items table without creating an
     * object.
     *
     * This version of the method automatically generates a primary key for the
     * new record. Use this method when adding brand new Items records to the
     * system.
     *
     * @param db_ The database into which to insert the record.
     * @param itemType_ `itemTypeId` of the associated ItemTypes record.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                              long itemType_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO Items"
                                     + " (itemType)"
                                     + " VALUES (?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemType_);
        return insertAndRetrieveLongKey(db_, statement);
    }

    /** @brief Insert a new record into the Items table without creating an
     * object.
     *
     * This version of the method allows the caller to specify a primary key for
     * the record. It should only be used for copying data between database
     * instances. Do not use this method for creating brand new Items records.
     *
     * @param db_ The database into which to insert the record.
     * @param itemId_ The primary key for the record. May not be 0.
     * @param itemType_ `itemTypeId` of the associated ItemTypes record.
     * 
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                              long itemId_,
                              long itemType_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO Items"
                                     + " (itemId, itemType)"
                                     + " VALUES (?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemId_);
        statement.setLong(2, itemType_);
        return insertAndRetrieveLongKey(db_, statement);
    }


    /* ACCESSORS */
    /** Unique ID for the item.
     * @return itemId
     */
    public long getId () {
        return itemId;
    }

    /** The item type associated with this item.
     * @return record IDed by itemType
     */
    public ItemTypesRecord getType () {
        return itemType;
    }

    public Timestamp getLastModified () {
        return lastHistory.getDatetime();
    }

    public boolean isOnMat () {
        return lastHistory.isOnMat();
    }

    public MatsRecord getMat () {
        if (isOnMat())
            return lastHistory.getMat();
        else
            return null;
    }

    public EventType getEventType () {
        return lastHistory.getEventType();
    }

    public Double[] getSensors () {
        return lastHistory.getSensors();
    }

    public Double getWeight () {
        return lastHistory.getWeight();
    }

    public Double getX () {
        if (isOnMat())
            return lastHistory.getX();
        else
            return null;
    }

    public Double getY () {
        if (isOnMat())
            return lastHistory.getY();
        else
            return null;
    }

    public HistoryRecord lastHistory () {
        return lastHistory;
    }


    /* STANDARD METHODS */
    public String toString () {
        return "Items<"
            + Long.toString(itemId) + ", "
            + "type: " + itemType.getName() + ", "
            + "weight: " + getWeight()
            + ">";
    }
}
