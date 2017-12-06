package db;

import db.Db;
import db.TableRecord;

public class Mat extends TableRecord { // !-- IP
    /* PROPERTIES */
    private int id;
    public int getId () {return id;}

    /* CONSTRUCTORS */ // !-- IP
    public Mat (int id_init) {
        id = id_init;
    }
}
