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

public class MainActivity extends BaseActivity {

    public static int MINIMAL_VERSION_OF_DB = 23;

    BusListAdapter busAdapter;
    RecyclerView mainList;
    AppDatabase db;

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

        onDatabaseChanged();
        mainList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, ((LinearLayoutManager) mainList.getLayoutManager()).getOrientation());
        mainList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    void onDatabaseChanged() {
        db = AppDatabase.getInstance(this);
        mainList = findViewById(R.id.mainList);
        busAdapter = new BusListAdapter(this, db.getDao().getCompaniesForList());
        mainList.setAdapter(busAdapter);
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
            holder.busDastiny.setText(item.l_begin + " –> " + item.l_end);
        }

        private void onItemClicked(int pos) {
            AppDao.BusInfoForList item = busList.get(pos);

            AppDatabase base = AppDatabase.getInstance(MainActivity.this);

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
            //Toast.makeText(MainActivity.this, ("Kliknięto " + item.name), Toast.LENGTH_SHORT).show();
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

}