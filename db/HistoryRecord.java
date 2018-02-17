package db;

import java.sql.PreparedStatement;
import java.sql.Array;
import java.sql.Timestamp;

import java.sql.SQLException;

public class HistoryRecord extends TableRecord {
    /* INSERTION METHODS */
    public static HistoryKey insert (Db db,
                                     long item_,
                                     Timestamp datetime_,
                                     long mat_,
                                     long eventType_,
                                     Double[] sensors_,
                                     Double x_,
                                     Double y_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO History"
                                     + "(item, datetime, mat, eventtype,"
                                     + " sensors, x, y)"
                                     + " VALUES (?, ?, ?, ?, ?, ?, ?);");
        statement.setLong(1, item_);
        statement.setTimestamp(2, datetime_);
        statement.setLong(3, mat_);
        statement.setLong(4, eventType_);
        statement.setArray(5, db.conn.createArrayOf("DOUBLE", sensors_));
        statement.setDouble(6, x_);
        statement.setDouble(7, y_);

        if (statement.executeUpdate() == 0) return null;

        return new HistoryKey(item_, datetime_);
    }

    public static HistoryKey insert (Db db,
                                     HistoryKey key,
                                     long mat_,
                                     long eventType_,
                                     Double[] sensors_,
                                     Double x_,
                                     Double y_) throws SQLException {
        return insert(db, key.getItem(), key.getDatetime(), mat_, eventType_,
                      sensors_, x_, y_);
    }
}
                                     
