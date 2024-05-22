package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Locale;
import android.util.*;

public class System_Setting extends AppCompatActivity {

    private boolean useCustomFont = false;
    CheckBox Bopomofo_Chk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("系統設置");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        Bopomofo_Chk = (CheckBox) findViewById(R.id.BopomofoCheckBox);

        Bopomofo_Chk.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "HELLO", Toast.LENGTH_SHORT).show();
        });
    }
}