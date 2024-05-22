package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Personal_Information extends AppCompatActivity
implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    Calendar Birthday_Calendar = Calendar.getInstance();
    TextView Birthday;
    EditText Name, ParentName, ParentRelation, School, Height, Weight, PhoneModel;
    Spinner Sex, Grade, Handedness, Footedness;

    Button ConfirmInfoButton, ClearInfoButton;

    AlertDialog.Builder ConfirmInfoCheck, ClearInfoCheck;

    // DataStore
    MyDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("兒童基本資料填寫系統");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        Name = (EditText) findViewById(R.id.NameEdt);
        ParentName = (EditText) findViewById(R.id.ParentNameEdt);
        ParentRelation = (EditText) findViewById(R.id.ParentRelationEdt);
        School = (EditText) findViewById(R.id.SchoolEdt);
        Height = (EditText) findViewById(R.id.HeightEdt);
        Weight = (EditText) findViewById(R.id.WeightEdt);
        PhoneModel = (EditText) findViewById(R.id.PhoneModelEdt);
        Sex = (Spinner) findViewById(R.id.SexSpinner);
        Grade = (Spinner) findViewById(R.id.GradeSpinner);
        Handedness = (Spinner) findViewById(R.id.HandednessSpinner);
        Footedness = (Spinner) findViewById(R.id.FootednessSpinner);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.Sex, R.layout.spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sex.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Grade, R.layout.spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Grade.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.Handedness, R.layout.spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Handedness.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.Footedness, R.layout.spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Footedness.setAdapter(adapter4);

        Birthday = (TextView) findViewById(R.id.BirthdaySetTxv);
        Birthday.setOnClickListener(this);

        ConfirmInfoButton = (Button) findViewById(R.id.ConfirmInfoBtn);
        ConfirmInfoButton.setOnClickListener(this);

        ClearInfoButton = (Button) findViewById(R.id.ClearInfoBtn);
        ClearInfoButton.setOnClickListener(this);

        dataStore = new MyDataStore(this);
        // 顯示本機儲存的個人資料
        Name.setText(dataStore.getValue(MyDataStore.NAME_KEY));
        ParentName.setText(dataStore.getValue(MyDataStore.PARENT_NAME_KEY));
        ParentRelation.setText(dataStore.getValue(MyDataStore.PARENT_RELATION_KEY));
        School.setText(dataStore.getValue(MyDataStore.SCHOOL_KEY));
        Height.setText(dataStore.getValue(MyDataStore.HEIGHT_KEY));
        Weight.setText(dataStore.getValue(MyDataStore.WEIGHT_KEY));
        PhoneModel.setText(dataStore.getValue(MyDataStore.PHONEMODEL_KEY));
        Sex.setSelection(dataStore.getValue(MyDataStore.SEX_KEY));
        Grade.setSelection(dataStore.getValue(MyDataStore.GRADE_KEY));
        Handedness.setSelection(dataStore.getValue(MyDataStore.HANDEDNESS_KEY));
        Footedness.setSelection(dataStore.getValue(MyDataStore.FOOTEDNESS_KEY));
        Birthday.setText(dataStore.getValue(MyDataStore.BIRTHDAY_KEY));
    }

    @Override
    public void onClick(View view) {
        String NameString = Name.getText().toString();
        String ParentNameString = ParentName.getText().toString();
        String ParentRelationString = ParentRelation.getText().toString();
        String SchoolString = School.getText().toString();
        String HeightString = Height.getText().toString();
        String WeightString = Weight.getText().toString();
        String PhoneModelString = PhoneModel.getText().toString();
        String BirthdayString = Birthday.getText().toString();
        int SexArrayIndex = Sex.getSelectedItemPosition();
        int GradeArrayIndex = Grade.getSelectedItemPosition();
        int HandednessArrayIndex = Handedness.getSelectedItemPosition();
        int FootednessArrayIndex = Footedness.getSelectedItemPosition();
        if (view == Birthday) {
            new DatePickerDialog(this, this,
                    Birthday_Calendar.get(Calendar.YEAR),
                    Birthday_Calendar.get(Calendar.MONTH),
                    Birthday_Calendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (view == ConfirmInfoButton) {
            ConfirmInfoCheck = new AlertDialog.Builder(this);
            ConfirmInfoCheck
                    .setMessage("確定要修改基本資料嗎？")
                    .setPositiveButton("確認", (dialog, id) ->
                    {
                        // 寫入本機內容
                        dataStore.putValue(MyDataStore.NAME_KEY, NameString);
                        dataStore.putValue(MyDataStore.PARENT_NAME_KEY, ParentNameString);
                        dataStore.putValue(MyDataStore.PARENT_RELATION_KEY, ParentRelationString);
                        dataStore.putValue(MyDataStore.SEX_KEY, SexArrayIndex);
                        dataStore.putValue(MyDataStore.BIRTHDAY_KEY, BirthdayString);
                        dataStore.putValue(MyDataStore.SCHOOL_KEY, SchoolString);
                        dataStore.putValue(MyDataStore.GRADE_KEY, GradeArrayIndex);
                        dataStore.putValue(MyDataStore.HEIGHT_KEY, HeightString);
                        dataStore.putValue(MyDataStore.WEIGHT_KEY, WeightString);
                        dataStore.putValue(MyDataStore.HANDEDNESS_KEY, HandednessArrayIndex);
                        dataStore.putValue(MyDataStore.FOOTEDNESS_KEY, FootednessArrayIndex);
                        dataStore.putValue(MyDataStore.PHONEMODEL_KEY, PhoneModelString);
                        // 特殊用法
                        dataStore.putValue(MyDataStore.NEED_INIT_PERSONAL_INFO, false);

                        Toast.makeText(this, "成功修改基本資料", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else if (view == ClearInfoButton) {
            ClearInfoCheck = new AlertDialog.Builder(this);
            ClearInfoCheck
                    .setMessage("確定要清除基本資料嗎？")
                    .setPositiveButton("確認", (dialog, id) ->
                    {
                        // 還原初始設定
                        dataStore.putValue(MyDataStore.NAME_KEY, "");
                        dataStore.putValue(MyDataStore.PARENT_NAME_KEY, "");
                        dataStore.putValue(MyDataStore.PARENT_RELATION_KEY, "");
                        dataStore.putValue(MyDataStore.SEX_KEY, 0);
                        dataStore.putValue(MyDataStore.BIRTHDAY_KEY, "");
                        dataStore.putValue(MyDataStore.SCHOOL_KEY, "");
                        dataStore.putValue(MyDataStore.GRADE_KEY, 0);
                        dataStore.putValue(MyDataStore.HEIGHT_KEY, "");
                        dataStore.putValue(MyDataStore.WEIGHT_KEY, "");
                        dataStore.putValue(MyDataStore.HANDEDNESS_KEY, 0);
                        dataStore.putValue(MyDataStore.FOOTEDNESS_KEY, 0);
                        dataStore.putValue(MyDataStore.PHONEMODEL_KEY, "");
                        // 特殊用法
                        dataStore.putValue(MyDataStore.NEED_INIT_PERSONAL_INFO, true);

                        Toast.makeText(this, "成功清除基本資料", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        Birthday.setText(y + "-" + (m + 1) + "-" + d);
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