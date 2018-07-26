package piotrjwegrzyn.busy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.services.AppDownloaderService;
import piotrjwegrzyn.busy.services.DBDownloaderService;

public abstract class BaseActivity extends AppCompatActivity {

    private BroadcastReceiver dbListener, appListener;
    private boolean isDarkTheme;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("settings", MODE_PRIVATE);
        isDarkTheme = preferences.getBoolean("darkmode", false);
        if (isDarkTheme) {
            setTheme(R.style.DarkTheme);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isDarkTheme != preferences.getBoolean("darkmode", false))
            recreate();
    }

    public void registerAppListener() {

        if (appListener != null) {
            return;
        }

        appListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("success", false)) {
                    if (intent.getBooleanExtra("newest", false)) {
                        makeLongToast("Po pobraniu pliku, otwórz go i zainstaluj");
                    } else {
                        onDatabaseUpdateNoChanged();
                    }
                } else {
                    onDatabaseUpdateFailed();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(appListener, new IntentFilter(AppDownloaderService.ACTION_DOWNLOADER));
    }

    public void registerDbListener() {

        if (dbListener != null) {
            return;
        }

        dbListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getBooleanExtra("success", false)) {
                    if (intent.getBooleanExtra("changed", false)) {
                        onDatabaseChanged();
                    } else {
                        onDatabaseUpdateNoChanged();
                    }
                } else {
                    onDatabaseUpdateFailed();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(dbListener, new IntentFilter(DBDownloaderService.ACTION_DOWNLOADER));
    }

    public void unregisterDbListener() {
        if (dbListener != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(dbListener);
            dbListener = null;
        }
    }

    public void unregisterAppListener() {
        if (appListener != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(appListener);
            appListener = null;
        }
    }

    public void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void makeLongToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    void onDatabaseUpdateFailed() {

    }

    void onDatabaseUpdateNoChanged() {

    }

    void onDatabaseChanged() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterDbListener();
        unregisterAppListener();
    }
}
