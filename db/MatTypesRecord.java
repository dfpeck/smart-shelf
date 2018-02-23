package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

/** @brief Class to represent MatTypes table entries.
 */
public class MatTypesRecord extends TableRecord {
    protected String matTypeId, matTypeComment;

    /* INSERTION METHODS */
    /** @brief Insert a new record into the MatTypes table without creating a
     * new object.
     *
     * The caller must specify the primary key for the record.
     *
     * @param db_ The database into which to insert the record.
     * @param matTypeId_ The primary key for the MatType, typically a product
     * code. May not be the empty string ("").
     * @param matTypeComment_ Optional additional information about the MatType.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns the empty string ("").
     */
    public static String insert (Db db_,
                                 String matTypeId_,
                                 String matTypeComment_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO MatTypes"
                                     + " (matTypeId, matTypeComment)"
                                     + " VALUES (?, ?);");
        statement.setString(1, matTypeId_);
        statement.setString(2, matTypeComment_);
        statement.executeUpdate();
        return matTypeId_;
    }
}
