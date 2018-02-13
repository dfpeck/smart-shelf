package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

public class ItemTypesRecord {
    /* INSERTION METHODS */
    // public static int insert (Db db, int itemTypeId_, String itemTypeName_,
    //                           boolean isContainer_) { // !--
    // }

    public static long insert (Db db,
                               String itemTypeName_,
                               String itemTypeComment_,
                               boolean isContainer_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO ItemTypes"
                                     + " (itemTypeName, itemTypeComment, isContainer)"
                                     + " VALUES (?, ?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setString(1, itemTypeName_);
        statement.setString(2, itemTypeComment_);
        statement.setBoolean(3, isContainer_);
        statement.executeUpdate();
        ResultSet keys = statement.getGeneratedKeys();
        keys.next();
        return keys.getLong(1);
    }
}
