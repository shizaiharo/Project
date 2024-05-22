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
import android.widget.TextView;
import android.widget.Toast;

public class GraphTrailResult extends AppCompatActivity {

    TextView Oval_InsideScore, Oval_OutsideScore;
    TextView Eight_InsideScore, Eight_OutsideScore;
    TextView Butterfly_InsideScore, Butterfly_OutsideScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_trail_result);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("路徑臨摹測驗評估結果");
        ImageButton Back_Btn = (ImageButton) findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, TestInterface.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        String Oval_InsideStr, Oval_OutsideStr, Eight_InsideStr, Eight_OutsideStr, Butterfly_InsideStr, Butterfly_OutsideStr;
        Oval_InsideStr = getIntent().getExtras().getString("Oval_Inside");
        Oval_OutsideStr = getIntent().getExtras().getString("Oval_Outside");
        Eight_InsideStr = getIntent().getExtras().getString("Eight_Inside");
        Eight_OutsideStr = getIntent().getExtras().getString("Eight_Outside");
        Butterfly_InsideStr = getIntent().getExtras().getString("Butterfly_Inside");
        Butterfly_OutsideStr = getIntent().getExtras().getString("Butterfly_Outside");

        Oval_InsideScore = (TextView) findViewById(R.id.Oval_Inside);
        Oval_OutsideScore = (TextView) findViewById(R.id.Oval_Outside);
        Eight_InsideScore = (TextView) findViewById(R.id.Eight_Inside);
        Eight_OutsideScore = (TextView) findViewById(R.id.Eight_Outside);
        Butterfly_InsideScore = (TextView) findViewById(R.id.Butterfly_Inside);
        Butterfly_OutsideScore = (TextView) findViewById(R.id.Butterfly_Outside);

        Oval_InsideScore.setText("路徑內：" + Oval_InsideStr);
        Oval_OutsideScore.setText("路徑外：" + Oval_OutsideStr);
        Eight_InsideScore.setText("路徑內：" + Eight_InsideStr);
        Eight_OutsideScore.setText("路徑外：" + Eight_OutsideStr);
        Butterfly_InsideScore.setText("路徑內：" + Butterfly_InsideStr);
        Butterfly_OutsideScore.setText("路徑外：" + Butterfly_OutsideStr);

        TextView score1, score2, score3;
        score1 = findViewById(R.id.score_1);
        score2 = findViewById(R.id.score_2);
        score3 = findViewById(R.id.score_3);

        score1.setText("評估分數：" +String.valueOf(Eval_Function(Double.parseDouble(Oval_InsideStr), Double.parseDouble(Oval_OutsideStr), 1)));
        score2.setText("評估分數：" +String.valueOf(Eval_Function(Double.parseDouble(Eight_InsideStr), Double.parseDouble(Eight_OutsideStr), 1)));
        score3.setText("評估分數：" +String.valueOf(Eval_Function(Double.parseDouble(Butterfly_InsideStr), Double.parseDouble(Butterfly_OutsideStr), 1)));
    }

    public double Eval_Function(double inside, double outside, double alpha) {
        return inside / (1 + alpha * outside);
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