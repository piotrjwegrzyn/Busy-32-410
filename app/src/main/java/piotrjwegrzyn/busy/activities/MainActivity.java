package piotrjwegrzyn.busy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSharedPreferences("settings", MODE_PRIVATE).getInt("dbversion", 0) == 0) {

            startActivity(new Intent(this, WelcomeActivity.class));

            finish();

            return;
        }

        setContentView(R.layout.activity_main);

        registerDbListener();

        AppDatabase db = AppDatabase.getInstance(this);

        RecyclerView mainList = findViewById(R.id.mainList);

        mainList.setAdapter(new BusListAdapter(this, db.getDao().getFirmsName()));

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
            holder.busName.setText(item.name);
            holder.busDastiny.setText(item.start + " –> " + item.stop);
        }

        private void onItemClicked(int pos) {
            AppDao.BusInfoForList item = busList.get(pos);

            Intent i = new Intent(MainActivity.this, BusplanActivity.class);
            i.putExtra("company", item.name);
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