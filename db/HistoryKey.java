package db;

import java.sql.Timestamp;

/** @brief Class to represent compound keys from the History table
 */
public class HistoryKey {
    private final long itemId;
    private final Timestamp datetime;

    /* CONSTRUCTORS */
    public HistoryKey (long itemId_, Timestamp datetime_) {
        itemId = itemId_;
        datetime = datetime_;
    }

    /* GETTERS */
    /** @return The `itemId` of the Item record associated with this key
     */
    public long itemId () {
        return itemId;
    }

    /** @return The datetime associated with this record
     */
    public Timestamp datetime () {
        return (Timestamp) datetime.clone();
    }

    /* STANDARD FUNCTIONS */
    public String toString () {
        return "<" + Long.toString(itemId) + ", " + datetime.toString() + ">";
    }
}
