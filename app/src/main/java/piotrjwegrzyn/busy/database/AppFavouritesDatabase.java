package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(version = 1, entities = {Favourite.class})
public abstract class AppFavouritesDatabase extends RoomDatabase {

    private static final Object mutex = new Object();
    private static AppFavouritesDatabase INSTANCE = null;

    public static AppFavouritesDatabase getInstance(Context context) {
        synchronized (mutex) {
            if (INSTANCE != null) return INSTANCE;
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppFavouritesDatabase.class, "DATABASE_FAVOURITES.db")
                    .allowMainThreadQueries()
                    .build();
            return INSTANCE;

        }
    }

    public abstract AppFavouritesDao getDao();

}
