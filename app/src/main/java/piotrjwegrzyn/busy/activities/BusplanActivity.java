package piotrjwegrzyn.busy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.database.AppDao;
import piotrjwegrzyn.busy.database.AppDatabase;
import piotrjwegrzyn.busy.database.AppFavouritesDatabase;
import piotrjwegrzyn.busy.database.Favourite;

public class BusplanActivity extends BaseActivity {
    private int track_id, company_id;
    private int te_begin_id = -1;
    private int te_end_id = -1;
    private Button buttonBegin;
    private Button buttonEnd;
    private ImageButton buttonChangeDastiny;
    private List<AppDao.StopNameAndId> stopsOnTrack;
    private TextView hoursTextView;
    private BottomNavigationView navigation;
    private String hours;
    private AppDatabase base;
    private AppDao.StringHours hour;
    private AppFavouritesDatabase fav;
    MenuItem item2;

    private final View.OnClickListener chooseStopBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Wybierz przystanek")
                    .setSingleChoiceItems(
                            new ArrayAdapter<AppDao.StopNameAndId>(view.getContext(),
                                    android.R.layout.simple_list_item_1, stopsOnTrack), -1,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    AppDao.StopNameAndId item = stopsOnTrack.get(i);
                                    if (view == buttonBegin) {
                                        buttonBegin.setText(item.name);
                                        te_begin_id = item.id;
                                    } else if (view == buttonEnd) {
                                        buttonEnd.setText(item.name);
                                        te_end_id = item.id;
                                    } else throw new IllegalStateException();
                                    updateTracks();
                                }
                            })
                    .show();
        }
    };
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            updateTracks(item.getItemId());
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busplan);

        track_id = getIntent().getIntExtra("track", -1);
        base = AppDatabase.getInstance(this);
        fav = AppFavouritesDatabase.getInstance(this);
        company_id = base.getDao().getCompanyId(track_id);
        setTitle(base.getDao().getBusName(company_id));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        hoursTextView = findViewById(R.id.hoursTextView);
        hoursTextView.setMovementMethod(new ScrollingMovementMethod());

        //Toast.makeText(this, "Firma " + base.getDao().getBusName(company_id), Toast.LENGTH_SHORT).show();

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        te_begin_id = getIntent().getIntExtra("begin", -1);
        te_end_id = getIntent().getIntExtra("end", -1);

        buttonBegin = findViewById(R.id.buttonBegin);
        buttonChangeDastiny = findViewById(R.id.buttonChangeDastiny);
        buttonEnd = findViewById(R.id.buttonEnd);


        if (base.getDao().countTrackElements(track_id) == 2 && (te_begin_id == -1 && te_end_id == -1)) {
            te_begin_id = base.getDao().getStopsOnTrack(track_id).get(0).id;
            te_end_id = base.getDao().getStopsOnTrack(track_id).get(1).id;
            updateTracks();
        }

        if (te_begin_id != -1 && te_end_id != -1) {
            buttonBegin.setText(base.getDao().getTrackElement(te_begin_id));
            buttonEnd.setText(base.getDao().getTrackElement(te_end_id));
            updateTracks();
        }

        stopsOnTrack = AppDatabase.getInstance(this).getDao().getStopsOnTrack(track_id);

        buttonBegin.setOnClickListener(chooseStopBtnListener);
        buttonEnd.setOnClickListener(chooseStopBtnListener);

        buttonChangeDastiny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp1 = te_begin_id;
                te_begin_id = te_end_id;
                te_end_id = temp1;

                CharSequence temp2 = buttonBegin.getText();
                buttonBegin.setText(buttonEnd.getText());
                buttonEnd.setText(temp2);

                updateTracks();
            }
        });

        DrawableCompat.setTint(buttonChangeDastiny.getDrawable(), ContextCompat.getColor(this, isDarkTheme() ? R.color.white : R.color.black));
    }

    private String dbParser(String text) {
        if (text == null) {
            return "Nie kursuje";
        }

        StringBuilder builder = new StringBuilder();

        int j = 0;
        String[] string = text.split(" ");
        for (String s : string) {
            String[] sArray = s.split("-");

            builder.append(sArray[0]);
            builder.append(": ");
            builder.append(sArray[1]);

            if (sArray.length > 2) {
                for (int i = 2; i < sArray.length; i++) {
                    builder.append(", ");
                    builder.append(sArray[i]);
                }
            }
            j++;
            if (j < string.length) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    private void updateTracks() {
        updateTracks(navigation.getSelectedItemId());
    }

    private void updateTracks(int itemId) {
        if (te_begin_id == -1 || te_end_id == -1)
            return;

        if (te_begin_id == te_end_id) {
            hoursTextView.setText("Wybrano dwa te same przystanki");
            return;
        }
        updateTextView(itemId);
    }

    private void updateTextView(int itemId) {
        int te_begin_nr = base.getDao().getTrackElementNr(te_begin_id);
        int te_end_nr = base.getDao().getTrackElementNr(te_end_id);
        if (te_begin_nr < te_end_nr) {
            hour = base.getDao().getToEndHours(te_begin_id);
        } else {
            hour = base.getDao().getToBeginHours(te_begin_id);
        }

        switch (itemId) {
            case R.id.week:
                hours = dbParser(hour.h_week);
                break;
            case R.id.saturday:
                hours = dbParser(hour.h_saturday);
                break;
            case R.id.sunday:
                hours = dbParser(hour.h_sunday);
                break;
            default:
                hours = dbParser(hour.h_week);
                break;
        }

        hoursTextView.setText(hours);
    }

    void updateIcon() {
        if (fav.getDao().checkIfInTable(track_id, te_begin_id, te_end_id) > 0 || fav.getDao().checkIfInTable(track_id, te_end_id, te_begin_id) > 0) {
            item2.setIcon(R.drawable.ic_favorite);
        } else {
            item2.setIcon(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item2 = menu.add(101, 101, 1, "Zapisz");
        updateIcon();
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (te_begin_id != 0 && te_end_id != 0) {
                    if (fav.getDao().checkIfInTable(track_id, te_begin_id, te_end_id) != 0 || fav.getDao().checkIfInTable(track_id, te_end_id, te_begin_id) != 0) {
                        fav.getDao().deleteFavourite(track_id, te_begin_id, te_end_id);
                        fav.getDao().deleteFavourite(track_id, te_end_id, te_begin_id);
                    } else {
                        Favourite f = new Favourite();
                        f.setValues(track_id, te_begin_id, te_end_id);
                        fav.getDao().putFavourite(f);
                    }
                }
                updateIcon();
                return true;
            }
        });
        MenuItem item = menu.add(100, 100, 1, "Informacje");
        item.setIcon(R.drawable.ic_info);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent h = new Intent(BusplanActivity.this, BusinfoActivity.class);
                h.putExtra("track", track_id);
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
}