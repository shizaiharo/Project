package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.TextView;

public class TranscriptionResult extends AppCompatActivity {

    TextView Xie_Time_Val, Liu_Time_Val, Gui_Time_Val;
    TextView Xie_Pred_Str_Val, Liu_Pred_Str_Val, Gui_Pred_Str_Val;
    TextView Xie_Strokes_Val, Liu_Strokes_Val, Gui_Strokes_Val;

    TextView Xie_Score_Val, Liu_Score_Val, Gui_Score_Val;

    final String[] Correct_Text = {"寫", "劉", "龜"};
    final int[] Correct_Strokes = {15, 15, 16};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcription_result);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("文字謄寫測驗評估結果");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, TestInterface.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        Xie_Time_Val = (TextView) findViewById(R.id.Xie_Time);
        Liu_Time_Val = (TextView) findViewById(R.id.Liu_Time);
        Gui_Time_Val = (TextView) findViewById(R.id.Gui_Time);

        Xie_Pred_Str_Val = (TextView) findViewById(R.id.Xie_Pred_Str);
        Liu_Pred_Str_Val = (TextView) findViewById(R.id.Liu_Pred_Str);
        Gui_Pred_Str_Val = (TextView) findViewById(R.id.Gui_Pred_Str);

        Xie_Strokes_Val = (TextView) findViewById(R.id.Xie_Strokes);
        Liu_Strokes_Val = (TextView) findViewById(R.id.Liu_Strokes);
        Gui_Strokes_Val = (TextView) findViewById(R.id.Gui_Strokes);

        Xie_Score_Val = (TextView) findViewById(R.id.Xie_Score);
        Liu_Score_Val = (TextView) findViewById(R.id.Liu_Score);
        Gui_Score_Val = (TextView) findViewById(R.id.Gui_Score);

        Xie_Time_Val.setText("測驗耗時：" + getIntent().getExtras().getString("Xie_Time"));
        Liu_Time_Val.setText("測驗耗時：" + getIntent().getExtras().getString("Liu_Time"));
        Gui_Time_Val.setText("測驗耗時：" + getIntent().getExtras().getString("Gui_Time"));
        Xie_Pred_Str_Val.setText("預測文字：" + getIntent().getExtras().getString("Xie_Pred_Str"));
        Liu_Pred_Str_Val.setText("預測文字：" + getIntent().getExtras().getString("Liu_Pred_Str"));
        Gui_Pred_Str_Val.setText("預測文字：" + getIntent().getExtras().getString("Gui_Pred_Str"));
        Xie_Strokes_Val.setText("筆畫數目：" + getIntent().getExtras().getString("Xie_Strokes"));
        Liu_Strokes_Val.setText("筆畫數目：" + getIntent().getExtras().getString("Liu_Strokes"));
        Gui_Strokes_Val.setText("筆畫數目：" + getIntent().getExtras().getString("Gui_Strokes"));

        double Xie_Score = 0, Liu_Score = 0, Gui_Score = 0;
        if (getIntent().getExtras().getString("Xie_Pred_Str").equals(Correct_Text[0])) {
            Xie_Score += 0.6;
        }
        if (getIntent().getExtras().getString("Liu_Pred_Str").equals(Correct_Text[1])) {
            Liu_Score += 0.6;
        }
        if (getIntent().getExtras().getString("Gui_Pred_Str").equals(Correct_Text[2])) {
            Gui_Score += 0.6;
        }
        Xie_Score += 0.3 * (1 - (((Math.abs(Integer.parseInt(getIntent().getExtras().getString("Xie_Strokes")) - Correct_Strokes[0]))) / (double) Correct_Strokes[0]));
        Liu_Score += 0.3 * (1 - (((Math.abs(Integer.parseInt(getIntent().getExtras().getString("Liu_Strokes")) - Correct_Strokes[1]))) / (double) Correct_Strokes[1]));
        Gui_Score += 0.3 * (1 - (((Math.abs(Integer.parseInt(getIntent().getExtras().getString("Gui_Strokes")) - Correct_Strokes[2]))) / (double) Correct_Strokes[2]));

        Xie_Score += 0.1 * Double.parseDouble(getIntent().getExtras().getString("Xie_SIM"));
        Liu_Score += 0.1 * Double.parseDouble(getIntent().getExtras().getString("Liu_SIM"));
        Gui_Score += 0.1 * Double.parseDouble(getIntent().getExtras().getString("Gui_SIM"));

        Xie_Score_Val.setText(String.valueOf(Xie_Score));
        Liu_Score_Val.setText(String.valueOf(Liu_Score));
        Gui_Score_Val.setText(String.valueOf(Gui_Score));
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