package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Hours")
public class Hour {
    @PrimaryKey(autoGenerate = true)
    public int h_id;

    String h_week;
    String h_saturday;
    String h_sunday;
}
