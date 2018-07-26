package piotrjwegrzyn.busy.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Objects;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.services.AppDownloaderService;
import piotrjwegrzyn.busy.services.DBDownloaderService;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        registerDbListener();
        registerAppListener();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    void onDatabaseUpdateFailed() {
        makeToast("Nie udało się zsynchronizować");
    }

    @Override
    void onDatabaseChanged() {
        makeToast("Uaktualniono dane");
    }

    @Override
    void onDatabaseUpdateNoChanged() {
        makeToast("Aktualna wersja jest najnowsza");
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

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getPreferenceManager().setSharedPreferencesName("settings");
            addPreferencesFromResource(R.xml.preferences);

            PreferenceScreen screen = getPreferenceScreen();

            getPreferenceByKey(screen, "updateDB").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DBDownloaderService.downloadDatabase(getActivity());
                    return true;
                }
            });

            getPreferenceByKey(screen, "updateApp").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AppDownloaderService.downloadApp(getActivity());
                    return true;
                }
            });

            getPreferenceByKey(screen, "sendEmail").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "piotrwegrzyn@protonmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Aplikacja Busy 32-410");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(emailIntent, "Wyślij mail'a"));
                    return true;
                }
            });
            getPreferenceByKey(screen, "github").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/piotrjwegrzyn/Busy-32-410"));
                    startActivity(intent);
                    return true;
                }
            });

        }

        Preference getPreferenceByKey(PreferenceScreen preferenceScreen, String key) {
            int count = preferenceScreen.getPreferenceCount();
            for (int i = 0; i < count; i++) {
                Preference preference = preferenceScreen.getPreference(i);
                if (Objects.equals(preference.getKey(), key))
                    return preference;
            }
            return null;
        }
    }


}