package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

/** @brief Class to represent MatTypes table entries.
 */
public class MatTypesRecord extends TableRecord {
    /* INSERTION METHODS */
    /** @brief Insert a new record into the MatTypes table without creating a
     * new object.
     *
     * The caller must specify the primary key for the record.
     *
     * @param db The database into which to insert the record.
     * @param matTypeId_ The primary key for the MatType, typically a product
     * code.
     * @param matTypeComment_ Optional additional information about the MatType.
     *
     * @return The primary key of the newly inserted record.
     */
    public static String insert (Db db,
                                 String matTypeId_,
                                 String matTypeComment_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO MatTypes"
                                     + " (matTypeId, matTypeComment)"
                                     + " VALUES (?, ?);");
        statement.setString(1, matTypeId_);
        statement.setString(2, matTypeComment_);
        if (statement.executeUpdate() == 0)
            return "";
        return matTypeId_;
    }
}
