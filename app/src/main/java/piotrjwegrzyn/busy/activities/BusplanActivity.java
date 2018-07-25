package piotrjwegrzyn.busy.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;

public class BusplanActivity extends BaseActivity {

    boolean defaultDestination = true;
    String[] busTable;
    private TextView mTextMessage;
    private BottomNavigationView navigation;
    private Button destination;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (defaultDestination) {
                return navUpdater(3, item);
            } else {
                return navUpdater(6, item);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busplan);

        mTextMessage = findViewById(R.id.textview);
        destination = findViewById(R.id.destination);
        String company = getIntent().getStringExtra("company");

        AppDatabase base = AppDatabase.getInstance(this);
        busTable = sqlParser(base.getDao().getBusplan(company));

        setTitle(busTable[0]);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        destination.setText("Kierunek: " + busTable[1] + " –> " + busTable[2]);

        mTextMessage.setText(dbParser(busTable[3]));


        mTextMessage.setMovementMethod(new ScrollingMovementMethod());
        Toast.makeText(this, "Firma " + company, Toast.LENGTH_SHORT).show();
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private String[] sqlParser(AppDao.BusplanInfo busplan) {
        String[] metadata = {busplan.name, busplan.start, busplan.stop, busplan.week1, busplan.saturday1, busplan.sunday1, busplan.week2, busplan.saturday2, busplan.sunday2};
        return metadata;
    }


    private String dbParser(String text) {
        if (text == null) {
            return "Nie kursuje";
        }

        String endText = "";
        int j = 0;
        String[] string = text.split(" ");
        for (String s : string) {
            String[] sArray = s.split("-");

            endText = endText + sArray[0] + ": " + sArray[1];

            if (sArray.length > 2) {
                for (int i = 2; i < sArray.length; i++) {
                    endText = endText + ", " + sArray[i];
                }
            }
            j++;
            if (j < string.length) {
                endText = endText + "\n";
            }
        }

        return endText;
    }

    private void setButtonText() {
        if (defaultDestination) {
            destination.setText("Kierunek: " + busTable[1] + " –> " + busTable[2]);
        } else {
            destination.setText("Kierunek: " + busTable[2] + " –> " + busTable[1]);
        }
    }

    private boolean navUpdater(int x, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.week:
                mTextMessage.setText(dbParser(busTable[x]));
                return true;
            case R.id.saturday:
                mTextMessage.setText(dbParser(busTable[x + 1]));
                return true;
            case R.id.sunday:
                mTextMessage.setText(dbParser(busTable[x + 2]));
                return true;
        }
        return false;
    }

    public void buttonListener(View view) {
        defaultDestination = !defaultDestination;
        navigation.setSelectedItemId(navigation.getSelectedItemId());
        setButtonText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(100, 100, 1, "Informacje");
        item.setIcon(R.drawable.ic_info);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent h = new Intent(BusplanActivity.this, BusinfoActivity.class);
                h.putExtra("company", busTable[0]);
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
}