package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Tracks")
public class Track {
    @PrimaryKey(autoGenerate = true)
    public int t_id;

    @ForeignKey(entity = Company.class, parentColumns = "c_id", childColumns = "c_owner")
    int c_owner;

    @ForeignKey(entity = Locality.class, parentColumns = "l_id", childColumns = "l_begin")
    int l_begin;

    @ForeignKey(entity = Locality.class, parentColumns = "l_id", childColumns = "l_end")
    int l_end;

    String t_infoshort;
    String t_infolong;
    String t_name;

}
