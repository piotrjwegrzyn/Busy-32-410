package piotrjwegrzyn.busy.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;

public class BusinfoActivity extends BaseActivity {

    AppDao.ShortBusInfo busInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businfo);

        String company = getIntent().getStringExtra("company");

        setTitle(company + " - Informacje");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AppDatabase base = AppDatabase.getInstance(this);
        busInfo = base.getDao().getShortBusInfo(company);

        TextView mTextView = findViewById(R.id.infoTextViewCompany);
        TextView mail = findViewById(R.id.infoMail);
        TextView number = findViewById(R.id.infoNumber);
        TextView www = findViewById(R.id.infoWww);

        if (busInfo.info != null) {
            mTextView.setText(busInfo.info);
        } else {
            mTextView.setText("Brak dodatkowych objaśnień");
        }
        mail.setText("Mail: " + busInfo.mail);
        number.setText("Nr. tel: " + busInfo.number);
        www.setText("Strona: " + busInfo.www);
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
}
