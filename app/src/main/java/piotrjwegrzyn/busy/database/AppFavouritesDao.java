package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AppFavouritesDao {

    String f1 = "SELECT COUNT(*) FROM Favourites";

    String f2 = "SELECT * FROM Favourites";

    String f4 = "DELETE FROM Favourites WHERE Favourites.t_owner = :t_owner AND Favourites.te_begin_id = :te_begin_id AND Favourites.te_end_id = :te_end_id";

    String f5 = "SELECT COUNT(*) FROM Favourites WHERE Favourites.t_owner = :t_owner AND Favourites.te_begin_id = :te_begin_id AND Favourites.te_end_id = :te_end_id";


    @Insert(onConflict = REPLACE)
    void putFavourite(Favourite favourite);

    @Query(f1)
    int countFavourites();

    @Query(f2)
    List<FavouriteBusForList> getFavourites();

    @Query(f4)
    void deleteFavourite(int t_owner, int te_begin_id, int te_end_id);

    @Query(f5)
    int checkIfInTable(int t_owner, int te_begin_id, int te_end_id);


    class FavouriteBusForList {
        public int f_id;
        public int t_owner;
        public int te_begin_id;
        public int te_end_id;
    }

}
