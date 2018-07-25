package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Localities")
public class Locality {
    @PrimaryKey(autoGenerate = true)
    public int l_id;

    String l_name;
    String l_gmina;
    String l_powiat;
}