package piotrjwegrzyn.busy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;
import piotrjwegrzyn.busy.database.AppFavouritesDao;
import piotrjwegrzyn.busy.database.AppFavouritesDatabase;

public class MainActivity extends BaseActivity {

    public static int MINIMAL_VERSION_OF_DB = 23;

    BusListAdapter busAdapter;
    FavouritesListAdapter favAdapter;
    RecyclerView mainList, favList;
    AppDatabase base;
    AppFavouritesDatabase fav;
    TextView textViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSharedPreferences("settings", MODE_PRIVATE).getInt("VERSION_DATABASE", 0) == 0) {

            Intent a = new Intent(this, WelcomeActivity.class);
            a.putExtra("title", "Pierwsze uruchomienie");
            a.putExtra("text", "Witaj!\nAby aplikacja działała, należy pobrać dane.\nZrób to klikając w poniższy przycisk:");
            startActivity(a);

            finish();

            return;
        } else {
            if (getSharedPreferences("settings", MODE_PRIVATE).getInt("VERSION_DATABASE", 0) < MINIMAL_VERSION_OF_DB) {
                Intent a = new Intent(this, WelcomeActivity.class);
                a.putExtra("title", "Nowa wersja aplikacji");
                a.putExtra("text", "Witaj ponownie!\nMusimy pobrać nowe dane.\nZrób to klikając w poniższy przycisk:");
                startActivity(a);

                finish();

                return;
            }
        }

        setContentView(R.layout.activity_main);

        registerDbListener();
        registerFavListener();

        base = AppDatabase.getInstance(this);
        fav = AppFavouritesDatabase.getInstance(this);
        mainList = findViewById(R.id.mainList);
        favList = findViewById(R.id.favouritiesList);

        onDatabaseChanged();

        mainList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, ((LinearLayoutManager) mainList.getLayoutManager()).getOrientation());
        mainList.addItemDecoration(dividerItemDecoration);
        favList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration2 = new DividerItemDecoration(this, ((LinearLayoutManager) favList.getLayoutManager()).getOrientation());
        favList.addItemDecoration(dividerItemDecoration2);
    }

    @Override
    void onDatabaseChanged() {
        busAdapter = new BusListAdapter(this, base.getDao().getCompaniesForList());
        mainList.setAdapter(busAdapter);
        showFavourites();
    }

    @Override
    void showFavourites() {
        favAdapter = new FavouritesListAdapter(this, fav.getDao().getFavourites());
        favList.setAdapter(favAdapter);
        if (favAdapter.getItemCount() == 0) {
            findViewById(R.id.busListFavourites).setVisibility(View.GONE);
        } else {
            findViewById(R.id.busListFavourites).setVisibility(View.VISIBLE);
        }
    }

    private String textBuilder(String l_begin, String l_end, String l_by, int c_owner) {
        if (l_begin != null && l_end != null) {
            if (l_by != null && base.getDao().countTracks(c_owner) == 1) {
                return l_begin + " –> " + l_end + " przez " + l_by;
            }
            return l_begin + " –> " + l_end;
        }
        return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(100, 100, 1, "Ustawienia");
        item.setIcon(R.drawable.ic_settings);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
        });
        return true;
    }

    class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {

        LayoutInflater inflater;
        List<AppDao.BusInfoForList> busList;

        BusListAdapter(Context context, List<AppDao.BusInfoForList> list) {
            inflater = LayoutInflater.from(MainActivity.this);
            this.busList = list;
        }

        @Override
        public int getItemCount() {
            return busList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.bus_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppDao.BusInfoForList item = busList.get(position);
            holder.busName.setText(item.c_name);
            holder.busDastiny.setText(textBuilder(item.l_begin, item.l_end, item.l_by, item.c_id));
        }

        private void onItemClicked(int pos) {
            AppDao.BusInfoForList item = busList.get(pos);

            Intent i;

            if (base.getDao().countTracks(item.c_id) > 1) {
                //to do
                i = new Intent(MainActivity.this, BustracksActivity.class);
                i.putExtra("company", item.c_id);
            } else {
                i = new Intent(MainActivity.this, BusplanActivity.class);
                i.putExtra("track", base.getDao().selectTracksId(item.c_id)[0]);
            }
            startActivity(i);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView busName, busDastiny;

            ViewHolder(View v) {
                super(v);
                busName = v.findViewById(R.id.busName);
                busDastiny = v.findViewById(R.id.busDastiny);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(getAdapterPosition());
                    }
                });
            }
        }
    }

    class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.ViewHolder> {

        LayoutInflater inflater2;
        List<AppFavouritesDao.FavouriteBusForList> favouritesList;

        FavouritesListAdapter(Context context, List<AppFavouritesDao.FavouriteBusForList> list) {
            inflater2 = LayoutInflater.from(MainActivity.this);
            this.favouritesList = list;
        }

        @Override
        public int getItemCount() {
            return favouritesList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater2.inflate(R.layout.bus_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppFavouritesDao.FavouriteBusForList item = favouritesList.get(position);

            int c_id = base.getDao().getCompanyId(item.t_owner);

            holder.busName.setText(item.c_name);
            holder.busDestination.setText(textBuilder(base.getDao().getTrackElement(item.te_begin_id), base.getDao().getTrackElement(item.te_end_id), null, c_id));
        }

        private void onItemClicked(int pos) {
            AppFavouritesDao.FavouriteBusForList item = favouritesList.get(pos);

            Intent i = new Intent(MainActivity.this, BusplanActivity.class);

            i.putExtra("company", base.getDao().getCompanyId(item.t_owner));
            i.putExtra("track", item.t_owner);
            i.putExtra("begin", item.te_begin_id);
            i.putExtra("end", item.te_end_id);

            startActivity(i);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView busName, busDestination;

            ViewHolder(View v) {
                super(v);
                busName = v.findViewById(R.id.busName);
                busDestination = v.findViewById(R.id.busDastiny);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClicked(getAdapterPosition());
                    }
                });
            }
        }
    }

}