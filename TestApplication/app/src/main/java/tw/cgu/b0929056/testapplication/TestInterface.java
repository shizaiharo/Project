package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class TestInterface extends AppCompatActivity
        implements View.OnClickListener {
    Button ImitationGraph_Btn, GraphTrail_Btn, TextTranscription_Btn, ParentSurvey_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_interface);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("請選擇測驗項目");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        ImitationGraph_Btn = findViewById(R.id.ImitationGraphBtn);
        GraphTrail_Btn = findViewById(R.id.GraphTrailBtn);
        TextTranscription_Btn = findViewById(R.id.TextTranscriptionBtn);
        ParentSurvey_Btn = findViewById(R.id.ParentSurveyBtn);

        ImitationGraph_Btn.setOnClickListener(this);
        GraphTrail_Btn.setOnClickListener(this);
        TextTranscription_Btn.setOnClickListener(this);
        ParentSurvey_Btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ImitationGraphBtn) {
            Intent it = new Intent();
            it.setClass(this, ImitationGraphTest.class);
            Bundle bundle = new Bundle();
            bundle.putString("Next_TestName", "Circle");
            it.putExtras(bundle);
            startActivity(it);
        }
        else if (view.getId() == R.id.GraphTrailBtn) {
            Intent it = new Intent();
            it.setClass(this, GraphTrailTest.class);
            Bundle bundle = new Bundle();
            bundle.putString("Next_TestName", "Oval");
            it.putExtras(bundle);
            startActivity(it);
        } else if (view.getId() == R.id.TextTranscriptionBtn) {
            Intent it = new Intent();
            it.setClass(this, TranscriptionTest.class);
            Bundle bundle = new Bundle();
            bundle.putString("Next_TestName", "Xie");
            it.putExtras(bundle);
            startActivity(it);
        } else if (view.getId() == R.id.ParentSurveyBtn) {
            startActivity(new Intent(this, ParentSurvey.class));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setMessage("是否要結束應用程式？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("否", null)
                    .create()
                    .show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}