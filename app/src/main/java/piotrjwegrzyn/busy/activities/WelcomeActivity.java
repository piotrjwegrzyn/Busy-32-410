package piotrjwegrzyn.busy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import piotrjwegrzyn.busy.services.DBDownloaderService;
import piotrjwegrzyn.busy.R;

public class WelcomeActivity extends BaseActivity {

    Button dbButton;
    TextView dbTextView;

    @Override
    void onDatabaseChanged() {
        Toast.makeText(this, "Pobrano pomyślnie!", Toast.LENGTH_SHORT).show();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    void onDatabaseUpdateFailed() {
        dbButton.setEnabled(true);
        dbButton.setText("Spróbuj ponownie");
        dbTextView.setText("Wystąpił błąd.\nSprawdź połączenie z internetem.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        setTitle("Pierwsze uruchomienie");

        dbButton = findViewById(R.id.dbButton);
        dbTextView = findViewById(R.id.dbTextView);

        registerDbListener();

        dbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbButton.setEnabled(false);
                dbTextView.setText("Trwa pobieranie...");
                DBDownloaderService.downloadDatabase(WelcomeActivity.this);
            }
        });
    }
}
