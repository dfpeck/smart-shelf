package db;

import java.sql.ResultSet;
import java.sql.SQLException;

import db.Db;
import db.TableRecord;

public class ItemType extends TableRecord { // !-- IP
    /* PROPERTIES */
    private int id;
    public int getId () {
        return id;
    }

    private String name;
    public String getName () {
        return name;
    }

    private boolean isContainer;
    public boolean isContainer () {
        return isContainer;
    }

    /* CONSTRUCTORS */ // !-- more constructors?
    public ItemType (Db db_init,
                     int id_init, String name_init, boolean isContainer_init) {
        db = db_init;
        id = id_init;
        name = name_init;
        isContainer = isContainer_init;
    }
    public ItemType (Db db_init, ResultSet rs) { // !-- IP
        db = db_init;
        try {
            id = rs.getInt("itypeid");
            name = rs.getString("itypename");
            isContainer = rs.getBoolean("iscontainer");
        }
        catch (SQLException e) {

        }
    }
}
