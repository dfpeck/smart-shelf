package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

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
                        HistoryRecord lastHistory_,
                        boolean genId_) {
        db = db_;
        itemId = itemId_;
        itemType = itemType_;
        lastHistory = lastHistory_;
        genId = genId_;
    }

    public ItemsRecord (Db db_, ItemTypesRecord itemType_) {
        this(db_, 0, itemType_, null, true);
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

    /** @brief Select all items that are on any mat.
     *
     * @param db_ database to select from
     * @return array of selected records
     */
    public static ItemsRecord[] selectOnMat (Db db_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("SELECT Items.* FROM"
                                      + " Items, History,"
                                      + " (SELECT item, MAX(datetime) AS maxtime"
                                      + " FROM History GROUP BY item) Latest"
                                      + " WHERE History.item = Latest.item"
                                      + " AND History.datetime = Latest.maxtime"
                                      + " AND Items.itemId = History.item"
                                      + " AND (eventType = 0 OR eventType = 2)",
                                      ResultSet.TYPE_SCROLL_INSENSITIVE,
                                      ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery();

        int row = 0;
        ItemsRecord[] records = new ItemsRecord[countRecords(rs)];
        while (rs.next())
            records[row++] = new ItemsRecord(db_, rs);

        return records;
    }

    // /** @brief Select all items on a particular mat.
    //  *
    //  * @param db_ database to select from
    //  * @param matId integer ID of the relevant mat
    //  * @return List of selected records
    //  */
    // public static List<ItemsRecord> selectOnMat (Db db_, long matId) {
    //     PreparedStatement statement =
    //         db_.conn.prepareStatement("SELECT Items.* FROM"
    //                                   + " Items, History,"
    //                                   + " (SELECT item, MAX(datetime) AS maxtime"
    //                                   + " FROM History GROUP BY item) Latest"
    //                                   + " WHERE History.item = Latest.item"
    //                                   + " AND History.datetime = Latest.maxtime"
    //                                   + " AND Items.itemId = History.item"
    //                                   + " AND (eventType = 0 OR eventType = 2)",
    //                                   ResultSet.TYPE_SCROLL_INSENSITIVE,
    //                                   ResultSet.CONCUR_READ_ONLY);
    //     ResultSet rs = statement.executeQuery();

    //     int row = 0;
    //     ItemsRecord[] records = new ItemsRecord[countRecords(rs)];
    //     while (rs.next())
    //         records[row++] = new ItemsRecord(db_, rs);

    //     return records;
    // }

    /** @brief Select all items on a particular mat.
     *
     * @param db_ database to select from
     * @param mat object representation of the relevant mat
     * @return List of selected records
     */
    // public static List<ItemsRecord> selectOnMat (Db db_, MatsRecord mat) {
        
    // }


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
     * @return The primary key of the newly inserted record.
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
     * @return The primary key of the newly inserted record.
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

    /** @brief Insert a record representing this Item into the Items table.
     *
     * @return The primary key of the newly inserted record.
     */
    public long insert () throws SQLException {
        if (genId)
            itemId = ItemsRecord.insert(db, itemType.getId());
        else
            ItemsRecord.insert(db, itemId, itemType.getId());
        genId = false;
        return itemId;
    }


    /* ACCESSORS */
    /** @brief Unique ID for the item. */
    public long getId () {
        return itemId;
    }

    /** @brief The item type associated with this item. */
    public ItemTypesRecord getType () {
        return itemType;
    }

    /** @brief The last time this item was modified. */
    public Timestamp getLastModified () {
        return lastHistory.getDatetime();
    }

    /** @brief Whether the item is on a mat or not. */
    public boolean isOnMat () {
        return lastHistory.isOnMat();
    }

    /** @brief The mat the item is on.
     *
     * Returns `null` if the item is not on a mat.
     */
    public MatsRecord getMat () {
        if (isOnMat())
            return lastHistory.getMat();
        else
            return null;
    }

    /** @brief The weight of the item. **/
    public Double getWeight () {
        return lastHistory.getWeight();
    }

    // /** @brief The x-coordinate of the item on its mat.
    //  *
    //  * Returns `null` if the item is not on a mat.
    //  */
    // public Double getX () {
    //     if (isOnMat())
    //         return lastHistory.getX();
    //     else
    //         return null;
    // }

    // /** @brief The y-coordinate of the item on its mat.
    //  *
    //  * Returns `null` if the item is not on a mat.
    //  */
    // public Double getY () {
    //     if (isOnMat())
    //         return lastHistory.getY();
    //     else
    //         return null;
    // }

    // /** @brief The coordinates of the item on its mat.
    //  *
    //  * Returns `null` if the item is not on a mat.
    //  */
    // public Double[] getCoords () {
    //     return lastHistory.getCoords();
    // }

    /** @brief The most recent History record associated with the item. */
    public HistoryRecord getLastHistory () {
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
