package piotrjwegrzyn.busy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import piotrjwegrzyn.busy.R;
import piotrjwegrzyn.busy.services.DBDownloaderService;

public class WelcomeActivity extends BaseActivity {

    Button dbButton;
    TextView dbTextView, dbInfo;

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

        setTitle(getIntent().getStringExtra("title"));

        dbButton = findViewById(R.id.dbButton);
        dbTextView = findViewById(R.id.dbTextView);
        dbInfo = findViewById(R.id.dbInfo);
        dbInfo.setText(getIntent().getStringExtra("text"));

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
