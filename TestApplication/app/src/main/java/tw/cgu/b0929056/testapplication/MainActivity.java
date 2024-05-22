package tw.cgu.b0929056.testapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
implements View.OnClickListener {
    Activity context = this;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    Button FineMotorTest_Btn, GrossMotorTest_Btn, PersonalInfo_Btn, SystemSetting_Btn, Logout_Btn;
    TextView UserName;
     

    // DataStore
    MyDataStore dataStore;

    private static final int REQUEST_VIDEO_CAPTURE = 1;
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    Date curDate = new Date(System.currentTimeMillis());

    // Video URI
    Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("主頁面");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, LoginActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Set navigationView Touch Event
        navigationView.setNavigationItemSelectedListener(item -> {
            // 點選時收起選單
            drawerLayout.closeDrawer(GravityCompat.START);

            // Get Item Id
            int id = item.getItemId();

            // 依照id判斷點了哪個項目並觸發相應事件
            if (id == R.id.action_home) {
                Toast.makeText(MainActivity.this, "首頁", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.action_help) {
                Toast.makeText(MainActivity.this, "使用說明", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.action_settings) {
                Toast.makeText(MainActivity.this, "設定", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.action_about) {
                Toast.makeText(MainActivity.this, "關於", Toast.LENGTH_SHORT).show();
            }
            return false;
        });

        UserName = (TextView) findViewById(R.id.UserNameTxv);

        FineMotorTest_Btn = (Button) findViewById(R.id.FineMotorTestBtn);
        GrossMotorTest_Btn = (Button) findViewById(R.id.GrossMotorTestBtn);
        PersonalInfo_Btn = (Button) findViewById(R.id.PersonalInfoBtn);
        SystemSetting_Btn = (Button) findViewById(R.id.SystemSettingBtn);
        Logout_Btn = (Button) findViewById(R.id.LogoutBtn);

        FineMotorTest_Btn.setOnClickListener(this);
        GrossMotorTest_Btn.setOnClickListener(this);
        PersonalInfo_Btn.setOnClickListener(this);
        SystemSetting_Btn.setOnClickListener(this);
        Logout_Btn.setOnClickListener(this);

        // 特殊用法
        dataStore = new MyDataStore(this);
        try {
            if (dataStore.getValue(MyDataStore.NEED_INIT_PERSONAL_INFO)) {
                Init();
            }
        } catch (Exception e) { // DataStore has null Value
            Init();
        }

        try {
            if ((dataStore.getValue(MyDataStore.NAME_KEY)).length() > 0) {
                UserName.setText(dataStore.getValue(MyDataStore.NAME_KEY) + " 您好～");
            } else {
                UserName.setText("訪客 您好～");
            }
        } catch (Exception e) {
            UserName.setText("訪客 您好～");
        }

        // Gross Motor
        boolean Start_Recording_Flag;

        try {
            Start_Recording_Flag = Objects.equals(getIntent().getExtras().getString("Start_Recording"), "True");
        } catch (Exception e) {
            Start_Recording_Flag = false;
        }
        if (Start_Recording_Flag) {
            int Gross_Motor_Index = getIntent().getExtras().getInt("Index");
            Intent it = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            Bundle bundle = new Bundle();
            bundle.putInt("Index", Gross_Motor_Index);
            it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, "GM" + (Gross_Motor_Index + 1) + "_" + getResources().getStringArray(R.array.Grade)[dataStore.getValue(MyDataStore.GRADE_KEY)] + "_" + dataStore.getValue(MyDataStore.NAME_KEY) + "_" + formatter.format(curDate) + ".mp4");
                contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                it.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            }
            startActivityForResult(it, REQUEST_VIDEO_CAPTURE);
        }
    }

    public void Init() {
        // 寫入本機內容
        dataStore.putValue(MyDataStore.NAME_KEY, "");
        dataStore.putValue(MyDataStore.PARENT_NAME_KEY, "");
        dataStore.putValue(MyDataStore.PARENT_RELATION_KEY, "");
        dataStore.putValue(MyDataStore.SEX_KEY, 0);
        dataStore.putValue(MyDataStore.BIRTHDAY_KEY, "未設定");
        dataStore.putValue(MyDataStore.SCHOOL_KEY, "");
        dataStore.putValue(MyDataStore.GRADE_KEY, 0);
        dataStore.putValue(MyDataStore.HEIGHT_KEY, "");
        dataStore.putValue(MyDataStore.WEIGHT_KEY, "");
        dataStore.putValue(MyDataStore.HANDEDNESS_KEY, 0);
        dataStore.putValue(MyDataStore.FOOTEDNESS_KEY, 0);
        dataStore.putValue(MyDataStore.PHONEMODEL_KEY, Build.MANUFACTURER + "_" + Build.MODEL);

        // 尚未填寫同意書
        dataStore.putValue(MyDataStore.CHILD_CONSENT_SIGNED, true);
        dataStore.putValue(MyDataStore.PARTICIPANT_CONSENT_SIGNED, true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.FineMotorTestBtn) {
            try {
                if ((dataStore.getValue(MyDataStore.CHILD_CONSENT_SIGNED)) && (dataStore.getValue(MyDataStore.PARTICIPANT_CONSENT_SIGNED))) {
                    startActivity(new Intent(this, TestInterface.class));
                } else {
                    startActivity(new Intent(this, SignConsent.class));
                }
            } catch (Exception e) {
                startActivity(new Intent(this, SignConsent.class));
            }
        } else if (view.getId() == R.id.GrossMotorTestBtn) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_VIDEO_CAPTURE);
            GrossMotorDialog dialog = new GrossMotorDialog();
            dialog.show(getSupportFragmentManager(), "Gross_Motor");
        } else if (view.getId() == R.id.PersonalInfoBtn) {
            startActivity(new Intent(this, Personal_Information.class));
        } else if (view.getId() == R.id.SystemSettingBtn) {
            startActivity(new Intent(this, System_Setting.class));
        } else if (view.getId() == R.id.LogoutBtn) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                videoUri = data.getData();
                if (videoUri != null) {
                    int Gross_Motor_Index = getIntent().getExtras().getInt("Index");
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Video.Media.DISPLAY_NAME, "GM" + (Gross_Motor_Index + 1) + "_" + getResources().getStringArray(R.array.Grade)[dataStore.getValue(MyDataStore.GRADE_KEY)] + "_" + dataStore.getValue(MyDataStore.NAME_KEY) + "_" + formatter.format(curDate) + ".mp4");
                    contentResolver.update(videoUri, values, null, null);
                }
            }
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
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