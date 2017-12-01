package db;

import db.Db;
import java.sql.ResultSet;

public class ItemType { // !-- IP
    /* PROPERTIES */
    private int;
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

    /* CONSTRUCTORS */
    public ItemType (Db db_init,
                     int id_init, String name_init, boolean isContainer_init) {
        db = db_init;
        id = id_init;
        name = name_init;
        isContainer = isContainer_init;
    }
    public ItemType (Db db_init, ResultSet rs) {
        db = db_init;
        id = rs.getInt("itypeid");
        name = rs.getString("itypename");
        isContainer = rs.getBoolean("iscontainer");
    }
}
