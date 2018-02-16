package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

abstract class TableRecord {
    protected static long insertAndRetrieveLongKey
        (Db db, PreparedStatement statement) throws SQLException {
        if (statement.executeUpdate() == 0)
            return 0;
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getLong(1);
    }

    protected static String insertAndRetrieveStringKey
        (Db db, PreparedStatement statement) throws SQLException {
        if (statement.executeUpdate() == 0)
            return "";
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getString(1);
    }
}
