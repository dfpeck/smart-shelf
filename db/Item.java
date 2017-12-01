package db;

import db.TableRecord;
import db.ItemType;
// import db.History;

public class Item extends TableRecord {
    /* PROPERTIES */
    private int id;
    public int getId () {
        return id;
    }

    private ItemType itemType;
    public ItemType getItemType () {
        return itemType;
    }

    // private History latestHistory;
    // public History getLatestHistory () {
        
    // }
}
