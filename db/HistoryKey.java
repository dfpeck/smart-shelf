package db;

import java.sql.Timestamp;

/** @brief Class to represent compound keys from the History table
 */
public class HistoryKey {
    private final long item;
    private final Timestamp datetime;

    /* CONSTRUCTORS */
    public HistoryKey (long item_, Timestamp datetime_) {
        item = item_;
        datetime = datetime_;
    }

    /* GETTERS */
    /** @return The `itemId` of the Item record associated with this key
     */
    public long getItem () {
        return item;
    }

    /** @return The datetime associated with this record
     */
    public Timestamp getDatetime () {
        return (Timestamp) datetime.clone();
    }

    /* STANDARD FUNCTIONS */
    public String toString () {
        return "<" + Long.toString(item) + ", " + datetime.toString() + ">";
    }
}
