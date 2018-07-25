package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface AppDao {


    @Query("SELECT Firms.name, Firms.start, Firms.stop FROM Firms ORDER BY Firms.name")
    List<BusInfoForList> getFirmsName();

    @Query("SELECT Firms.name, Firms.start, Firms.stop, Firms.week1, Firms.saturday1, Firms.sunday1, Firms.week2, Firms.saturday2, Firms.sunday2 FROM Firms WHERE Firms.name=:name LIMIT 1")
    BusplanInfo getBusplan(String name);

    @Query("SELECT Firms.name, Firms.info, Firms.mail, Firms.number, Firms.www FROM Firms WHERE Firms.name=:name LIMIT 1")
    ShortBusInfo getShortBusInfo(String name);

    public class BusInfoForList {
        public String name;
        public String start;
        public String stop;
    }

    public class BusplanInfo {
        public String name;
        public String start;
        public String stop;
        public String week1;
        public String saturday1;
        public String sunday1;
        public String week2;
        public String saturday2;
        public String sunday2;
    }

    public class ShortBusInfo {
        public String name;
        public String info;
        public String mail;
        public String number;
        public String www;
    }
}
