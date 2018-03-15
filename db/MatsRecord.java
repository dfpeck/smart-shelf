package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

/** @brief Class to represent Mats table entries.
 */
public class MatsRecord extends TableRecord {
    protected long matId;
    protected MatTypesRecord matType;
    protected String matComment;


    /* CONSTRUCTORS */
    public MatsRecord (Db db_,
                       long matId_,
                       MatTypesRecord matType_,
                       String matComment_) {
        db = db_;
        matId = matId_;
        matType = matType_;
        matComment = matComment_;
    }


    /* INESRTION METHODS */
    /** @brief Insert a new record into the Mats table without creating an
     * object.
     *
     * This version of the method automatically generates a primary key for the
     * new record. Use this method when adding brand new Mats records to the
     * system.
     *
     * @param db_ The database into which to insert the record.
     * @param matType_ `matTypeId` of the associated MatTypes record.
     * @param matComment_ Optinal additional information about the Mat.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                               String matType_,
                               String matComment_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO Mats"
                                     + " (matType, matComment)"
                                     + " VALUES (?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, matType_);
        statement.setString(2, matComment_);
        return insertAndRetrieveLongKey(db_, statement);
    }

    /** @brief Insert a new record into the Mats table without creating an
     * object.
     *
     * This version of the record allows the caller to specify a primary key for
     * the record. It should only be used for copying data between database
     * instances. Do not use this method for creating brand new Mats records.
     *
     * @param db_ The database into which to insert the record.
     * @param matId_ The primary key for the record. May not be 0.
     * @param matType_ `matTypeId` of the associated MatTypes record.
     * @param matComment_ Optinal additional information about the Mat.
     *
     * @return The primary key of the newly inserted record. If the insert
     * fails, returns 0.
     */
    public static long insert (Db db_,
                               long matId_,
                               String matType_,
                               String matComment_) throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("INSERT INTO Mats"
                                     + " (matId, matType, matComment)"
                                     + " VALUES (?, ?, ?);");
        statement.setLong(1, matId_);
        statement.setString(2, matType_);
        statement.setString(3, matComment_);
        statement.executeUpdate();
        return matId_;
    }
}
