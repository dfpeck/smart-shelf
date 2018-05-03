package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Array;

import java.sql.SQLException;

abstract class TableRecord {
    Db db;

    protected static long insertAndRetrieveLongKey
        (Db db, PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
        ResultSet key = statement.getGeneratedKeys();
        key.next();
        return key.getLong(1);
    }

    protected static ResultSet
        getAdjustedResultSet (ResultSet rs, int row) throws SQLException {
        rs.absolute(row);
        return rs;
    }

    protected static ResultSet selectByIdLong (Db db_, long id,
                                               String table, String idColumn)
        throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("SELECT * FROM " + table
                                      + " WHERE " + idColumn + " = ?;");
        statement.setLong(1, id);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs;
    }

    protected static ResultSet selectByIdString (Db db_, String id,
                                                 String table, String idColumn)
        throws SQLException {
        PreparedStatement statement =
            db_.conn.prepareStatement("SELECT * FROM " + table
                                      + " WHERE " + idColumn + " = ?;");
        statement.setString(1, id);
        ResultSet rs = statement.executeQuery();
        rs.next();
        return rs;
    }

    protected static Double [] sqlArrayToDoubleArray (Array arr) throws SQLException {
        Object[] objArr = (Object[]) arr.getArray();
        Double[] dblArr = new Double[objArr.length];

        for (int i=0; i < objArr.length; i++)
            dblArr[i] = Double.parseDouble(objArr[i].toString());

        return  dblArr;
    }

    public static int countRecords (ResultSet rs) throws SQLException {
        int rows;
        if (rs.last())
             rows = rs.getRow();
        else
            rows = 0;
        rs.beforeFirst();

        return rows;
    }
}
