package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Track_elements")
public class Track_element {
    @PrimaryKey(autoGenerate = true)
    public int te_id;

    @ForeignKey(entity = Track.class, parentColumns = "t_id", childColumns = "t_owner")
    int t_owner;

    int te_nr;

    @ForeignKey(entity = Stop.class, parentColumns = "l_id", childColumns = "l_locality")
    int l_locality;

    @ForeignKey(entity = Hour.class, parentColumns = "h_id", childColumns = "h_toend")
    int h_toend;

    @ForeignKey(entity = Hour.class, parentColumns = "h_id", childColumns = "h_tobegin")
    int h_tobegin;
}
