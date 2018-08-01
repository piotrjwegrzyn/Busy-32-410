package piotrjwegrzyn.busy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;

public class BusinfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businfo);

        int c_id = getIntent().getIntExtra("company", -1);

        int t_id = getIntent().getIntExtra("track", 0);

        AppDatabase base = AppDatabase.getInstance(this);

        if (t_id == 0) {
            findViewById(R.id.busInfoTop).setVisibility(View.GONE);
            findViewById(R.id.infoTextViewCompany).setVisibility(View.GONE);
            findViewById(R.id.busInfoBottom).setVisibility(View.GONE);
        } else {
            TextView x = findViewById(R.id.infoTextViewCompany);
            String t = base.getDao().getTrackLongInfo(t_id);
            if (t == null) {
                t = "Brak dodatkowych informacji dotyczących trasy";
            }
            x.setText(t);
        }

        setTitle(base.getDao().getBusName(c_id) + " - Informacje");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<AppDao.CompanyInfo> informations = base.getDao().getCompanyInformations(c_id);

        RecyclerView infoList = findViewById(R.id.infoList);
        infoList.setAdapter(new InfoAdapter(informations));
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

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {

        AppDao.CompanyInfo[] list;
        LayoutInflater inflater;

        InfoAdapter(List<AppDao.CompanyInfo> information) {
            list = information.toArray(new AppDao.CompanyInfo[information.size()]);
            inflater = getLayoutInflater();
        }

        @Override
        public int getItemCount() {
            return list.length;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.bus_info_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            AppDao.CompanyInfo info = list[position];
            holder.infoValue.setText(info.d_value);
            holder.infoKey.setText(info.dt_type);
            holder.infoIcon.setImageResource(info.getIconRes());
            DrawableCompat.setTint(holder.infoIcon.getDrawable(), ContextCompat.getColor(BusinfoActivity.this, isDarkTheme() ? R.color.white : R.color.black));
        }

        private void onViewClicked(int pos) {
            AppDao.CompanyInfo info = list[pos];
            switch (info.dt_action) {
                case AppDao.CompanyInfo.NO_ACTION:
                    break;
                case AppDao.CompanyInfo.ACTION_WEB_PAGE: {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(normalizeUrl(info.d_value)));
                    startActivity(intent);
                    break;
                }
                case AppDao.CompanyInfo.ACTION_MAIL: {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", info.d_value, null));
                    startActivity(Intent.createChooser(emailIntent, "Wyślij mail'a"));
                    break;
                }
                case AppDao.CompanyInfo.ACTION_PHONE:
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + info.d_value));
                    startActivity(intent);
                    break;
                default:
                    throw new IllegalStateException("Unknown dt_action");
            }
        }


        private String normalizeUrl(String url) {
            if (url.startsWith("http://") || url.startsWith("https://"))
                return url;

            if (url.startsWith("www"))
                return "http://" + url;

            return "http://www." + url;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView infoKey;
            TextView infoValue;
            ImageView infoIcon;

            ViewHolder(View v) {
                super(v);
                infoIcon = v.findViewById(R.id.infoIcon);
                infoKey = v.findViewById(R.id.infoKey);
                infoValue = v.findViewById(R.id.infoValue);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onViewClicked(getAdapterPosition());
                    }
                });
            }
        }

    }
}
