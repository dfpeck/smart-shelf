package db;

import java.sql.Timestamp;

public class HistoryKey {
    private final long item;
    private final Timestamp datetime;

    /* CONSTRUCTORS */
    public HistoryKey (long item_, Timestamp datetime_) {
        item = item_;
        datetime = datetime_;
    }

    /* GETTERS */
    public long getItem () {
        return item;
    }

    public Timestamp getDatetime () {
        return (Timestamp) datetime.clone();
    }

    /* STANDARD FUNCTIONS */
    public String toString () {
        return "<" + Long.toString(item) + ", " + datetime.toString() + ">";
    }
}
