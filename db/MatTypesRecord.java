package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

public class MatTypesRecord extends TableRecord {
    /* INSERTION METHODS */
    public static String insert (Db db,
                                 String matTypeId_,
                                 String matTypeComment_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO MatTypes"
                                     + " (matTypeId, matTypeComment)"
                                     + " VALUES (?, ?);";
        statement.setString(1, matTypeId_);
        statement.setString(2, matTypeComment_);
        if (statement.executeUpdate() == 0)
            return "";
        return matTypeId_;
    }
}
