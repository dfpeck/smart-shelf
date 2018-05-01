package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

public enum EventType {
    ADDED,
    REMOVED,
    REPLACED,
    REDUCED,
    REFILLED;

    public void insert (Db db) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO EventTypes"
                                     + " (eventTypeId, eventTypeName)"
                                     + " VALUES (?, ?);");
        statement.setLong(1, ordinal());
        statement.setString(2, name());
        statement.executeUpdate();
    }
}
