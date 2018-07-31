package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "DatasTypes")
public class DatasType {
    @PrimaryKey(autoGenerate = true)
    int dt_id;
    String dt_type;
    int dt_action;
}
