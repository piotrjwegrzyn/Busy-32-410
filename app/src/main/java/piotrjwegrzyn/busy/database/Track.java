package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Tracks")
public class Track {
    @PrimaryKey(autoGenerate = true)
    public int t_id;

    @ForeignKey(entity = Company.class, parentColumns = "c_id", childColumns = "c_id")
    int c_id;

    @ForeignKey(entity = Stop.class, parentColumns = "s_id", childColumns = "s_begin")
    int s_begin;

    @ForeignKey(entity = Stop.class, parentColumns = "s_id", childColumns = "s_end")
    int s_end;

    String t_info;

}
