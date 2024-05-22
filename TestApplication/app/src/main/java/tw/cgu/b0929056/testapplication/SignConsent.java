package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SignConsent extends AppCompatActivity
implements View.OnClickListener {

    TextView Welcome_Txv;

    Button ChildConsent_Btn, ParticipantConsent_Btn, ConsentComplete_Btn;

    MyDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_consent);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("測驗同意書");
        ImageButton Back_Btn = (ImageButton) findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        ChildConsent_Btn = (Button) findViewById(R.id.ChildConsentBtn);
        ParticipantConsent_Btn = (Button) findViewById(R.id.ParticipantConsentBtn);
        ConsentComplete_Btn = (Button) findViewById(R.id.ConsentCompleteBtn);

        ChildConsent_Btn.setOnClickListener(this);
        ParticipantConsent_Btn.setOnClickListener(this);
        ConsentComplete_Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ChildConsentBtn) {

            startActivity(new Intent(this, Consent_ChildVer.class));

            //dataStore.putValue(MyDataStore.CHILD_CONSENT_SIGNED, true);

        } else if (view.getId() == R.id.ParticipantConsentBtn) {

            //dataStore.putValue(MyDataStore.PARTICIPANT_CONSENT_SIGNED, true);

        } else if (view.getId() == R.id.ConsentCompleteBtn) {

            Intent it = new Intent();
            it.setClass(this, TestInterface.class);
            startActivity(it);

        }
    }
}