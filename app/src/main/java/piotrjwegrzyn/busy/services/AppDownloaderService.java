package piotrjwegrzyn.busy.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AppDownloaderService extends IntentService {

    public static final String ACTION_DOWNLOADER = "appDownloader";
    static final String VERSION_ADRESS = "http://www.piotrjwegrzyn.droppages.com/appversion.txt";
    static final String APP_ADRESS = "http://www.piotrjwegrzyn.droppages.com/appadress.txt";

    public AppDownloaderService() {
        super("AppDownloaderService");
    }

    public static void downloadApp(Context context) {
        context.startService(new Intent(context, AppDownloaderService.class));
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

            String versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;

            String serverAppVersion = getServerAppVersion();

            if (serverAppVersion.equals(versionName)) {
                sendBackBroadcast(true, false);
                return;
            }

            //pobierz nową aplikację, bo aktualna jest stara

            downloadNewApp();

            sendBackBroadcast(true, true);

        } catch (Exception e) {
            e.printStackTrace();
            sendBackBroadcast(false, false);
        }
    }

    private String getServerAppVersion() throws Exception {
        URLConnection connection = new URL(VERSION_ADRESS).openConnection();
        int length = connection.getContentLength();
        if (length > 50)
            throw new IllegalStateException();
        InputStream stream = connection.getInputStream();
        byte[] buffer = new byte[length];
        int read = stream.read(buffer);
        stream.close();

        return (new String(buffer, "UTF-8").trim());
    }

    private void downloadNewApp() throws Exception {
        URLConnection connection = new URL(APP_ADRESS).openConnection();
        int length = connection.getContentLength();
        if (length > 50)
            throw new IllegalStateException();
        InputStream stream = connection.getInputStream();
        byte[] buffer = new byte[length];
        int read = stream.read(buffer);
        stream.close();

        String adress = new String(buffer, "UTF-8").trim();

        adress = "https://www.dropbox.com/" + adress + "/busy-32-410-piotrjwegrzyn.apk?dl=1";

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adress));
        startActivity(intent);
    }

    private void sendBackBroadcast(boolean success, boolean newest) {
        Intent i = new Intent();
        i.setAction(ACTION_DOWNLOADER);
        i.putExtra("success", success);
        i.putExtra("newest", newest);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }
}