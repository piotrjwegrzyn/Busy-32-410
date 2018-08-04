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
import java.util.Objects;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;

public class BustracksActivity extends BaseActivity {

    TrackListAdapter trackAdapter;
    RecyclerView trackList;
    AppDatabase base;
    int company_id;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bustracks);

        company_id = getIntent().getIntExtra("company", 0);
        base = AppDatabase.getInstance(this);

        setTitle(base.getDao().getBusName(company_id) + " - wybór trasy");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        trackList = findViewById(R.id.trackList);
        trackAdapter = new TrackListAdapter(this, base.getDao().getTracksForList(company_id));
        trackList.setAdapter(trackAdapter);
        trackList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, ((LinearLayoutManager) trackList.getLayoutManager()).getOrientation());
        trackList.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(100, 100, 1, "Informacje");
        item.setIcon(R.drawable.ic_info);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent h = new Intent(BustracksActivity.this, BusinfoActivity.class);
                h.putExtra("company", company_id);
                startActivity(h);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

        LayoutInflater inflater;
        List<AppDao.BusTracksForList> trackList;

        TrackListAdapter(Context context, List<AppDao.BusTracksForList> list) {
            inflater = LayoutInflater.from(BustracksActivity.this);
            this.trackList = list;
        }

        @Override
        public int getItemCount() {
            return trackList.size();
        }

        @Override
        public BustracksActivity.TrackListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BustracksActivity.TrackListAdapter.ViewHolder(inflater.inflate(R.layout.bus_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppDao.BusTracksForList item = trackList.get(position);
            if (item.t_name != null) {
                title = item.t_name + ", ";
            }
            holder.busDast.setText(title + item.l_begin + " –> " + item.l_end);
            holder.busShortInfo.setText("Przez " + item.t_infoshort);
        }

        private void onItemClicked(int pos) {
            AppDao.BusTracksForList item = trackList.get(pos);

            base = AppDatabase.getInstance(BustracksActivity.this);

            Intent i;
            i = new Intent(BustracksActivity.this, BusplanActivity.class);
            i.putExtra("track", item.t_id);

            startActivity(i);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView busDast, busShortInfo;

            ViewHolder(View v) {
                super(v);
                busDast = v.findViewById(R.id.busName);
                busShortInfo = v.findViewById(R.id.busDastiny);
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
