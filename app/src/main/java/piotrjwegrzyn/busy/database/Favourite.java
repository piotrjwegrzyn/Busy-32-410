package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Favourites")
public class Favourite {
    @PrimaryKey(autoGenerate = true)
    public int f_id;
    public int t_owner;
    public int te_begin_id;
    public int te_end_id;

    public Favourite() {

    }

    public void setValues(int t, int b, int e) {
        this.t_owner = t;
        this.te_begin_id = b;
        this.te_end_id = e;
    }
}