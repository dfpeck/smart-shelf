package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

public class ItemTypesRecord {
    /* INSERTION METHODS */
    public static long insert (Db db,
                              long itemTypeId_,
                              String itemTypeName_,
                              String itemTypeComment_,
                              boolean isContainer_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO ItemTypes"
                                     + " (itemTypeId, itemTypeName,"
                                     + " itemTypeComment, isContainer)"
                                     + " VALUES (?, ?, ?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemTypeId_);
        statement.setString(2, itemTypeName_);
        statement.setString(3, itemTypeComment_);
        statement.setBoolean(4, isContainer_);
        statement.executeUpdate();
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getLong(1);
    }

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
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getLong(1);
    }
}
