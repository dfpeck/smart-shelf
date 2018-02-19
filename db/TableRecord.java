package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

abstract class TableRecord {
    protected static long insertAndRetrieveLongKey
        (Db db, PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getLong(1);
    }
}
