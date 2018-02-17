package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

public class MatsRecord extends TableRecord {
    /* INESRTION METHODS */
    public static long insert (Db db,
                               String matType_,
                               String matComment_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO Mats"
                                     + " (matType, matComment)"
                                     + " VALUES (?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, matType_);
        statement.setString(2, matComment_);
        return insertAndRetrieveLongKey(db, statement);
    }

    public static long insert (Db db,
                               long matId_,
                               String matType_,
                               String matComment_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO Mats"
                                     + " (matId, matType, matComment)"
                                     + " VALUES (?, ?, ?);");
        statement.setLong(1, matId_);
        statement.setString(2, matType_);
        statement.setString(3, matComment_);
        if (statement.executeUpdate() == 0) return 0;
        return matId_;
    }
}
