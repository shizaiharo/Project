package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

public class GraphTrailTest extends AppCompatActivity
implements TextWatcher, DialogInterface.OnClickListener {

    Activity context = this;
    ReplicaGraphTrailCanvas CanvasView;
    String TestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_trail_test);
        CanvasView = findViewById(R.id.GraphTrailCanvasView);

        TestName = getIntent().getExtras().getString("Next_TestName");
        String Guidance_Title;

        switch (Objects.requireNonNull(TestName)) {
            case "Oval":
                Guidance_Title = "練習題：橢圓圖路徑臨摹測驗";
                CanvasView.setting_background(R.drawable.ovalgraphtrail, R.drawable.ovalgraphtrail_ans);
                break;
            case "Eight":
                Guidance_Title = "正式題（一）：數字八路徑臨摹測驗";
                CanvasView.setting_background(R.drawable.eightgraphtrail, R.drawable.eightgraphtrail_ans);
                break;
            case "Butterfly":
                Guidance_Title = "正式題（二）：蝴蝶圖路徑臨摹測驗";
                CanvasView.setting_background(R.drawable.butterflygraphtrail, R.drawable.butterflygraphtrail_ans);
                break;
            case "Finished":
                Guidance_Title = null;
                CanvasView.setting_background(R.drawable.emptycanvas, R.drawable.emptycanvas);
                Intent it = new Intent();
                it.setClass(this, GraphTrailResult.class);
                Bundle bundle = new Bundle();
                bundle.putString("Oval_Inside", getIntent().getExtras().getString("Oval_Inside"));
                bundle.putString("Oval_Outside", getIntent().getExtras().getString("Oval_Outside"));
                bundle.putString("Eight_Inside", getIntent().getExtras().getString("Eight_Inside"));
                bundle.putString("Eight_Outside", getIntent().getExtras().getString("Eight_Outside"));
                bundle.putString("Butterfly_Inside", getIntent().getExtras().getString("Butterfly_Inside"));
                bundle.putString("Butterfly_Outside", getIntent().getExtras().getString("Butterfly_Outside"));
                it.putExtras(bundle);
                startActivity(it);
                break;
            default:
                Guidance_Title = null;
                break;
        }

        AlertDialog.Builder Guidance = new AlertDialog.Builder(this);
        Guidance.setIcon(android.R.drawable.btn_star_big_on)
                .setTitle(Guidance_Title)
                .setMessage("請畫在路徑中，不可超出線外，並且一筆完成。")
                .setCancelable(false)
                .setPositiveButton("確認", null)
                .show();

        Button ClearButton = (Button) findViewById(R.id.ClearBtn);
        Button QuitButton = (Button) findViewById(R.id.QuitBtn);
        Button DoneButton = (Button) findViewById(R.id.DoneBtn);

        ClearButton.setOnClickListener(view -> {CanvasView.clear();});
        QuitButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage("是否要離開測驗？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(context, TestInterface.class));
                        }
                    })
                    .setNegativeButton("否", null)
                    .create()
                    .show();
        });
        DoneButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage("確定已完成測驗嗎？")
                    .setCancelable(false)
                    .setPositiveButton("確認", this)
                    .setNegativeButton("取消", null)
                    .show();
        });

        EditText ThicknessEdt = findViewById(R.id.BrushThicknessEdt);
        ThicknessEdt.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not Used
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditText ThicknessEdt = findViewById(R.id.BrushThicknessEdt);
        String ThicknessStr = ThicknessEdt.getText().toString();
        if (ThicknessStr.isEmpty() || ThicknessStr.equals(".")) {
            CanvasView.setBrushThickness(20);
        }
        else {
            CanvasView.setBrushThickness(Integer.parseInt(ThicknessStr));
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Not Used
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            try {
                CanvasView.save(this);
                Intent it = new Intent();
                it.setClass(this, GraphTrailTest.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
                Bundle bundle = new Bundle();
                switch (Objects.requireNonNull(TestName)) {
                    case "Oval":
                        bundle.putString("Next_TestName", "Eight");
                        bundle.putString("Oval_Inside", CanvasView.get_score()[0]);
                        bundle.putString("Oval_Outside", CanvasView.get_score()[1]);
                        break;
                    case "Eight":
                        bundle.putString("Next_TestName", "Butterfly");
                        bundle.putString("Oval_Inside", getIntent().getExtras().getString("Oval_Inside"));
                        bundle.putString("Oval_Outside", getIntent().getExtras().getString("Oval_Outside"));
                        bundle.putString("Eight_Inside", CanvasView.get_score()[0]);
                        bundle.putString("Eight_Outside", CanvasView.get_score()[1]);
                        break;
                    case "Butterfly":
                        bundle.putString("Next_TestName", "Finished");
                        bundle.putString("Oval_Inside", getIntent().getExtras().getString("Oval_Inside"));
                        bundle.putString("Oval_Outside", getIntent().getExtras().getString("Oval_Outside"));
                        bundle.putString("Eight_Inside", getIntent().getExtras().getString("Eight_Inside"));
                        bundle.putString("Eight_Outside", getIntent().getExtras().getString("Eight_Outside"));
                        bundle.putString("Butterfly_Inside", CanvasView.get_score()[0]);
                        bundle.putString("Butterfly_Outside", CanvasView.get_score()[1]);
                        break;
                    default:
                        break;
                }
                it.putExtras(bundle);
                startActivity(it);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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