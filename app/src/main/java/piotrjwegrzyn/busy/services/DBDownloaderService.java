package piotrjwegrzyn.busy.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import piotrjwegrzyn.busy.database.AppDatabase;


public class DBDownloaderService extends IntentService {

    public static final String ACTION_DOWNLOADER = "downloader";
    public static final String VERSION_ADDRESS = "http://www.piotrjwegrzyn.droppages.com/Busy/DATABASE/VERSION_DATABASE.txt";
    public static final String FILE_ADDRESS = "http://www.piotrjwegrzyn.droppages.com/Busy/DATABASE/DATABASE_MAIN.db";

    public DBDownloaderService() {
        super("DBDownloaderService");
    }

    public static void downloadDatabase(Context context) {
        context.startService(new Intent(context, DBDownloaderService.class));
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                sendBackBroadcast(false, false);
                return;
            }

            SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            int dbVersion = getDBVersion();

            if ((dbVersion == preferences.getInt("VERSION_DATABASE", 0))) {
                sendBackBroadcast(true, false);
                return;
            }

            //pobierz baze danych, bo aktualna jest stara

            downloadDB();

            preferences.edit().putInt("VERSION_DATABASE", dbVersion).commit();

            Thread.sleep(500);

            sendBackBroadcast(true, true);

        } catch (Exception e) {
            e.printStackTrace();
            sendBackBroadcast(false, false);
        }
    }

    private int getDBVersion() throws Exception {
        URLConnection connection = new URL(VERSION_ADDRESS).openConnection();
        int length = connection.getContentLength();
        if (length > 50)
            throw new IllegalStateException();
        InputStream stream = connection.getInputStream();
        byte[] buffer = new byte[length];
        int read = stream.read(buffer);
        stream.close();

        return Integer.valueOf(new String(buffer, "UTF-8").trim());
    }

    private void downloadDB() throws Exception {
        URLConnection connection = new URL(FILE_ADDRESS).openConnection();
        int length = connection.getContentLength();

        AppDatabase.getInstance(this).close();

        try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(getDatabasePath("DATABASE_MAIN.db"))) {

            byte[] buffer = new byte[2048];
            while (length > 0) {
                if (input.available() == 0)
                    Thread.sleep(10);

                int read = input.read(buffer);
                output.write(buffer, 0, read);
                length -= read;
            }
        }
    }

    private void sendBackBroadcast(boolean success, boolean changed) {
        Intent i = new Intent();
        i.setAction(ACTION_DOWNLOADER);
        i.putExtra("success", success);
        i.putExtra("changed", changed);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}
