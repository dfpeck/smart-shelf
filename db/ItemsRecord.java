package db;

import java.sql.PreparedStatement;

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
                        ItemTypesRecord itemType_//,
                        /*HistoryRecord lastHistory_*/) {
        db = db_;
        itemId = itemId_;
        itemType = itemType_;
        // lastHistory = lastHistory_;
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
}
