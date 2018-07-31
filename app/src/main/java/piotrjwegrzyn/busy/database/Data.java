package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Datas")
public class Data {
    @PrimaryKey(autoGenerate = true)
    public int d_id;

    @ForeignKey(entity = Company.class, parentColumns = "c_id", childColumns = "c_owner")
    int c_owner;

    int dt_owner;
    String d_value;

}
