package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(version = 1, entities = {Firm.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Object mutex = new Object();
    private static AppDatabase INSTANCE = null;

    public static AppDatabase getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE != null) return INSTANCE;
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "busyAppDB.db")
                    .allowMainThreadQueries()
                    .build();
            return INSTANCE;

        }
    }

    public abstract AppDao getDao();
}
