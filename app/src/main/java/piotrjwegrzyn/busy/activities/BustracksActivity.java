package piotrjwegrzyn.busy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;

public class BustracksActivity extends BaseActivity {

    TrackListAdapter trackAdapter;
    RecyclerView trackList;
    AppDatabase base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bustracks);

        int c_owner = getIntent().getIntExtra("company", 0);
        base = AppDatabase.getInstance(this);

        setTitle(base.getDao().getBusName(c_owner) + " - wybór trasy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trackList = findViewById(R.id.trackList);
        trackAdapter = new TrackListAdapter(this, base.getDao().getTracksForList(c_owner));
        trackList.setAdapter(trackAdapter);
        trackList.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, ((LinearLayoutManager) trackList.getLayoutManager()).getOrientation());
        trackList.addItemDecoration(dividerItemDecoration);
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
            holder.busDast.setText(item.l_begin + " –> " + item.l_end);
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
