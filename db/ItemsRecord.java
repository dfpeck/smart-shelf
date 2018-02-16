package db;

import java.sql.PreparedStatement;

import java.sql.SQLException;

public class ItemsRecord extends TableRecord {
    /* INSERTION METHODS */
    public static long insert (Db db,
                              long itemId_,
                              long itemType_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO Items"
                                     + " (itemId, itemType)"
                                     + " VALUES (?, ?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemId_);
        statement.setLong(2, itemType_);
        return insertAndRetrieveLongKey(db, statement);
    }

    public static long insert (Db db,
                              long itemType_) throws SQLException {
        PreparedStatement statement =
            db.conn.prepareStatement("INSERT INTO Items"
                                     + " (itemType)"
                                     + " VALUES (?);",
                                     PreparedStatement.RETURN_GENERATED_KEYS);
        statement.setLong(1, itemType_);
        return insertAndRetrieveLongKey(db, statement);

        /* !-- TODO methods that accept ItemTypesRecord object for itemType */
    }
}
