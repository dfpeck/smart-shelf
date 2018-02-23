package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

/** @brief Class to represent ItemTypes table entries.
 */
public class ItemTypesRecord extends TableRecord {
    protected Db db;
    protected long itemTypeId;
    protected String itemTypeName, itemTypeComment;
    protected boolean isContainer;

    /* INSERTION METHODS */
    /** @brief Insert a new record into the ItemTypes table without creating an object.
     *
     * This version of the method automatically generates a primary key for the
     * new record. Use this method when adding brand new ItemTypes records to
     * the system.
     *
     * @param db_ The database into which to insert the record.
     * @param itemTypeName_ User-friendly name for the ItemType.
     * @param itemTypeComment_ Optional additional information about the
     * ItemType.
     * @param isContainer_ Whether this type of item serves as a container.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                               String itemTypeName_,
                               String itemTypeComment_,
                               boolean isContainer_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO ItemTypes"
                                     + " (itemTypeName, itemTypeComment, isContainer)"
                                     + " VALUES (?, ?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, itemTypeName_);
        statement.setString(2, itemTypeComment_);
        statement.setBoolean(3, isContainer_);
        return insertAndRetrieveLongKey(db_, statement);
    }

    /** @brief Insert a new record into the ItemTypes table without creating an object.
     *
     * This version of the method allows the caller to specify a primary key for
     * the record. It should only be used for copying data between database
     * instances. Do not use this method for creating brand new ItemTypes
     * records.
     *
     * @param db_ The database into which to insert the record.
     * @param itemTypeId_ The primary key for the record. May not be 0.
     * @param itemTypeName_ User-friendly name for the ItemType.
     * @param itemTypeComment_ Optional additional information about the
     * ItemType.
     * @param isContainer_ Whether this type of item serves as a container.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                              long itemTypeId_,
                              String itemTypeName_,
                              String itemTypeComment_,
                              boolean isContainer_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO ItemTypes"
                                     + " (itemTypeId, itemTypeName,"
                                     + " itemTypeComment, isContainer)"
                                     + " VALUES (?, ?, ?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemTypeId_);
        statement.setString(2, itemTypeName_);
        statement.setString(3, itemTypeComment_);
        statement.setBoolean(4, isContainer_);
        return insertAndRetrieveLongKey(db_, statement);
    }
}
