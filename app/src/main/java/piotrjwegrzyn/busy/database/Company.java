package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Companies")
public class Company {
    @PrimaryKey(autoGenerate = true)
    public int c_id;

    String c_name;

    @ForeignKey(entity = Track.class, parentColumns = "t_id", childColumns = "t_main")
    int t_main;
}
