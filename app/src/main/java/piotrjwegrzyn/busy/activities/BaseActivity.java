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
import piotrjwegrzyn.busy.services.DBDownloaderService;

public abstract class BaseActivity extends AppCompatActivity {

    private BroadcastReceiver dbListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        if (preferences.getBoolean("darkmode", false)) {
            setTheme(R.style.DarkTheme);
        }
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

    public void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
    }
}
