package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

/** @brief Class to represent MatTypes table entries.
 */
public class MatTypesRecord extends TableRecord {
    protected String matTypeId, matTypeComment;


    /* CONSTRUCTORS */
    public MatTypesRecord (Db db_,
                           String matTypeId_,
                           String matTypeComment_) {
        db = db_;
        matTypeId = matTypeId_;
        matTypeComment = matTypeComment_;
    }

    public MatTypesRecord (Db db_, ResultSet rs) throws SQLException {
        this(db_, rs.getString("matTypeId"), rs.getString("matTypeComment"));
    }

    public MatTypesRecord (Db db_, ResultSet rs, int row) throws SQLException {
        this(db_, getAdjustedResultSet(rs, row));
    }


    /* SELECTION METHODS */
    public static MatTypesRecord
        selectById (Db db_, String matTypeId_) throws SQLException {
        return new MatTypesRecord(db_,  selectByIdString(db_, matTypeId_,
                                                         "MatTypes", "matTypeId"));
    }


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


    /* ACCESSORS */
    /** Unique ID for the mat type.
     * @return matTypeId
     */
    public String getId () {
        return matTypeId;
    }

    /** Comment describing the mat type.
     * @return matTypeComment
     */
    public String getComment () {
        return matTypeComment;
    }


    /* STANDARD METHODS */
    public String toString () {
        return "MatTypes<'" + matTypeId + "'>";
    }
}
