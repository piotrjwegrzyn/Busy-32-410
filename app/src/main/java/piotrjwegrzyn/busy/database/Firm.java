package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Firms")
public class Firm {
    @PrimaryKey(autoGenerate = true)
    public int id;

    String name;
    String start;
    String stop;

    String week1;
    String saturday1;
    String sunday1;
    String week2;
    String saturday2;
    String sunday2;

    // Businfo activity
    String info;
    String mail;
    String number;
    String www;

}
