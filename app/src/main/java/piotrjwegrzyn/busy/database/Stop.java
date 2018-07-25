package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Stops")
public class Stop {
    @PrimaryKey(autoGenerate = true)
    public int s_id;

    String s_name;

    @ForeignKey(entity = Locality.class, parentColumns = "l_id", childColumns = "l_locality")
    int l_locality;

    double s_latitude;
    double s_longitude;

}
