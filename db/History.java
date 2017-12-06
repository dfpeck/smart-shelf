package db;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.HashMap;

import db.Db;
import db.TableRecord;
import db.Item;
import db.Mat;

public class History extends TableRecord { // !-- IP
    private static boolean eventTypesInitialized = false;
    private static HashMap<Integer, String> eventTypes;

    private boolean initializeEventTypes () { // !-- IP
        ResultSet events;

        try {
            events = db.executeQuery("SELECT * FROM EventTypes;");
            while (events.next())
                eventTypes.put(events.getInt("eventid"),
                               events.getString("eventname"));
        }
        catch (SQLException e) {
            return false;
        }

        eventTypesInitialized = true;
        return true;
    }

    /* PROPERTIES */
    private Item item;
    public Item getItem () {return item;}

    private Timestamp datetime;
    public Timestamp getDatetime () {return datetime;}

    private Mat mat;
    public Mat getMat () {return mat;}

    private float[] sensors;
    public float getSensor (int index) {return sensors[index];}

    public float getx () {return getCoords()[0];}
    public float gety () {return getCoords()[1];}
    public float[] getCoords () { // !-- TODO
        float[] r = {0, 0};
        return r;
    }

    /* CONSTRUCTORS */ // !-- TODO
}
