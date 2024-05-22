package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

public class Consent_ChildVer extends AppCompatActivity
implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    TextView Consent_Title_ChildVer, Consent_ProjectName_ChildVer, Consent_Department_ChildVer, SubTitle_ChildVer, NameTxv_ChildVer, GenderTxv_ChildVer, BirthdayTxv_ChildVer, Consent_Detail_ChildVer;

    Calendar Birthday_Calendar = Calendar.getInstance();

    TextView Birthday;

    EditText Name;

    Spinner Gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_child_ver);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("受試者同意書（兒童版）");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        Name = (EditText) findViewById(R.id.NameEdt_ChildVer);

        Gender = (Spinner) findViewById(R.id.GenderSpinner_ChildVer);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Sex, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(adapter1);

        Birthday = (TextView) findViewById(R.id.BirthdaySetTxv_ChildVer);
        Birthday.setOnClickListener(this);

        Consent_Title_ChildVer = findViewById(R.id.Consent_Title_ChildVer);
        Consent_Title_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        Consent_ProjectName_ChildVer = findViewById(R.id.Consent_ProjectName_ChildVer);
        Consent_ProjectName_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        Consent_Department_ChildVer = findViewById(R.id.Consent_Department_ChildVer);
        Consent_Department_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        SubTitle_ChildVer = findViewById(R.id.SubTitle_ChildVer);
        SubTitle_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        NameTxv_ChildVer = findViewById(R.id.NameTxv_ChildVer);
        NameTxv_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        GenderTxv_ChildVer = findViewById(R.id.GenderTxv_ChildVer);
        GenderTxv_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        BirthdayTxv_ChildVer = findViewById(R.id.BirthdayTxv_ChildVer);
        BirthdayTxv_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
        Consent_Detail_ChildVer = findViewById(R.id.Consent_Detail_ChildVer);
        Consent_Detail_ChildVer.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/bopomofo.ttf"));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        Birthday.setText(y + "-" + (m + 1) + "-" + d);
    }

    @Override
    public void onClick(View view) {
        if (view == Birthday) {
            new DatePickerDialog(this, this,
                    Birthday_Calendar.get(Calendar.YEAR),
                    Birthday_Calendar.get(Calendar.MONTH),
                    Birthday_Calendar.get(Calendar.DAY_OF_MONTH)).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setMessage("是否要結束應用程式？")
                    .setPositiveButton("是", (dialogInterface, i) -> finishAffinity())
                    .setNegativeButton("否", null)
                    .create()
                    .show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}