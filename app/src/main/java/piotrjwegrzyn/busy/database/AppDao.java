package piotrjwegrzyn.busy.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import piotrjwegrzyn.busy.R;

@Dao
public interface AppDao {

    String s1 = "SELECT Companies.c_id, Companies.c_name , L_begin.l_name AS l_begin, L_end.l_name AS l_end, Tracks.t_infoshort AS l_by FROM Companies\n" +
            "INNER JOIN Tracks ON Tracks.t_id = Companies.t_main\n" +
            "INNER JOIN Localities AS L_begin ON L_begin.l_id = Tracks.l_begin\n" +
            "INNER JOIN Localities AS L_end ON L_end.l_id = Tracks.l_end ORDER BY Companies.c_name";

    String s2 = "SELECT Companies.c_name FROM Companies WHERE Companies.c_id=:c_id";

    String s3 = "SELECT dt_type, d_value, dt_action FROM Datas\n" +
            "INNER JOIN DatasTypes On DatasTypes.dt_id = Datas.dt_owner\n" +
            "WHERE Datas.c_owner = :c_id ORDER BY DatasTypes.dt_id";

    String s4 = "SELECT COUNT(*) FROM Tracks WHERE Tracks.c_owner=:c_id";

    String s5 = "SELECT t_id FROM Tracks WHERE Tracks.c_owner=:c_id";

    String s6 = "SELECT t_infolong FROM Tracks WHERE Tracks.t_id=:t_id";

    String s7 = "SELECT c_owner FROM Tracks WHERE Tracks.t_id=:track_id";

    String s8 = "SELECT Tracks.t_id, L_begin.l_name AS l_begin, L_end.l_name AS l_end, Tracks.t_infoshort FROM Tracks\n" +
            "INNER JOIN Localities AS L_begin ON L_begin.l_id = Tracks.l_begin\n" +
            "INNER JOIN Localities AS L_end ON L_end.l_id = Tracks.l_end\n" +
            "WHERE Tracks.c_owner=:c_id";

    String s9 = "SELECT Track_elements.te_id AS id, Localities.l_name AS name FROM Track_elements\n" +
            "INNER JOIN Localities ON Localities.l_id = Track_elements.l_locality\n" +
            "WHERE Track_elements.t_owner = :t_id ORDER BY Track_elements.te_nr";

    String s10 = "select l_name from Track_elements\n" +
            "INNER join Localities on Localities.l_id = Track_elements.l_locality\n" +
            "Where Track_elements.te_id =:te_id";

    String s11 = "SELECT COUNT(*) FROM Track_elements WHERE Track_elements.t_owner=:t_owner";

    String s12 = "SELECT te_nr FROM Track_elements WHERE Track_elements.te_id =:te_id";

    String s13 = "SELECT h_week, h_saturday, h_sunday FROM Hours \n" +
            "INNER JOIN Track_elements ON Track_elements.h_toend = Hours.h_id\n" +
            "WHERE Track_elements.te_id =:te_id";

    String s14 = "SELECT h_week, h_saturday, h_sunday FROM Hours \n" +
            "INNER JOIN Track_elements ON Track_elements.h_tobegin = Hours.h_id\n" +
            "WHERE Track_elements.te_id =:te_id";

    String s15 = "SELECT Tracks.t_name FROM Tracks INNER JOIN Companies ON Tracks.c_owner = Companies.c_id WHERE Companies.c_id = :c_owner";

    @Query(s1)
    List<BusInfoForList> getCompaniesForList();

    @Query(s2)
    String getBusName(int c_id);

    @Query(s3)
    List<CompanyInfo> getCompanyInformations(int c_id);

    @Query(s4)
    int countTracks(int c_id);

    @Query(s5)
    int[] selectTracksId(int c_id);

    @Query(s6)
    String getTrackLongInfo(int t_id);

    @Query(s7)
    int getCompanyId(int track_id);

    @Query(s8)
    List<BusTracksForList> getTracksForList(int c_id);

    @Query(s9)
    List<StopNameAndId> getStopsOnTrack(int t_id);

    @Query(s10)
    String getTrackElement(int te_id);

    @Query(s11)
    int countTrackElements(int t_owner);

    @Query(s12)
    int getTrackElementNr(int te_id);

    @Query(s13)
    StringHours getToEndHours(int te_id);

    @Query(s14)
    StringHours getToBeginHours(int te_id);

    @Query(s15)
    String[] getTracksNames(int c_owner);

    class StringHours {
        public String h_week;
        public String h_saturday;
        public String h_sunday;
    }

    class StopNameAndId {
        public int id;
        public String name;

        @Override
        public String toString() {
            return name;
        }
    }

    class BusInfoForList {
        public int c_id;
        public String c_name;
        public String l_begin;
        public String l_end;
        public String l_by;
    }

    class CompanyInfo {
        public final static int NO_ACTION = 0;
        public final static int ACTION_WEB_PAGE = 1;
        public final static int ACTION_MAIL = 2;
        public final static int ACTION_PHONE = 3;
        public String dt_type;
        public String d_value;
        public int dt_action;

        public int getIconRes() {
            switch (dt_action) {
                case NO_ACTION:
                    return R.drawable.ic_info_black_24dp;
                case ACTION_WEB_PAGE:
                    return R.drawable.ic_open_in_browser_black_24dp;
                case ACTION_MAIL:
                    return R.drawable.ic_mail_outline_black_24dp;
                case ACTION_PHONE:
                    return R.drawable.ic_phone_black_24dp;
                default:
                    throw new IllegalStateException("Unknown dt_action");
            }
        }
    }

    class BusTracksForList {
        public int t_id;
        public String l_begin;
        public String l_end;
        public String t_infoshort;
    }
}
