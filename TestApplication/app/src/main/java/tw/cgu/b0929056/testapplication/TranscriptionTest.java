package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.Ink;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TranscriptionTest extends AppCompatActivity
implements DialogInterface.OnClickListener {

    Activity context = this;
    ReplicaTranscriptionCanvas CanvasView;
    String TestName;

    // DataStore
    MyDataStore dataStore;

    // OCR
    private DigitalInkRecognizer recognizer;
    private final RemoteModelManager remoteModelManager = RemoteModelManager.getInstance();
    private DigitalInkRecognitionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcription_test);
        try {
            initializeRecognition();
        } catch (MlKitException e) {
            throw new RuntimeException(e);
        }

        CanvasView = findViewById(R.id.TranscriptionCanvasView);

        TestName = getIntent().getExtras().getString("Next_TestName");
        String Guidance_Title;

        ImageView Standard_Diagram = (ImageView) findViewById(R.id.Transcription_STD_Diagram);

        switch (Objects.requireNonNull(TestName)) {
            case "Xie":
                Guidance_Title = "正式題（一）：寫字謄寫測驗";
                Standard_Diagram.setImageResource(R.drawable.xie);
                break;
            case "Liu":
                Guidance_Title = "正式題（二）：劉字謄寫測驗";
                Standard_Diagram.setImageResource(R.drawable.liu);
                break;
            case "Gui":
                Guidance_Title = "正式題（三）：龜字謄寫測驗";
                Standard_Diagram.setImageResource(R.drawable.gui);
                break;
            case "Finished":
                Guidance_Title = null;
                Intent it = new Intent();
                it.setClass(this, TranscriptionResult.class);
                Bundle bundle = new Bundle();
                bundle.putString("Xie_Time", getIntent().getExtras().getString("Xie_Time"));
                bundle.putString("Liu_Time", getIntent().getExtras().getString("Liu_Time"));
                bundle.putString("Gui_Time", getIntent().getExtras().getString("Gui_Time"));
                bundle.putString("Xie_Pred_Str", getIntent().getExtras().getString("Xie_Pred_Str"));
                bundle.putString("Liu_Pred_Str", getIntent().getExtras().getString("Liu_Pred_Str"));
                bundle.putString("Gui_Pred_Str", getIntent().getExtras().getString("Gui_Pred_Str"));
                bundle.putString("Xie_Strokes", getIntent().getExtras().getString("Xie_Strokes"));
                bundle.putString("Liu_Strokes", getIntent().getExtras().getString("Liu_Strokes"));
                bundle.putString("Gui_Strokes", getIntent().getExtras().getString("Gui_Strokes"));
                bundle.putString("Xie_SIM", getIntent().getExtras().getString("Xie_SIM"));
                bundle.putString("Liu_SIM", getIntent().getExtras().getString("Liu_SIM"));
                bundle.putString("Gui_SIM", getIntent().getExtras().getString("Gui_SIM"));
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
                .setMessage("請寫出一模一樣的字。")
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

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.ConstraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        dataStore = new MyDataStore(this);

        if (dataStore.getValue(MyDataStore.HANDEDNESS_KEY) == 2) { // Left Handedness
            constraintSet.connect(CanvasView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            constraintSet.connect(CanvasView.getId(), ConstraintSet.END, R.id.GuideLine_First, ConstraintSet.END);
            constraintSet.connect(Standard_Diagram.getId(), ConstraintSet.START, R.id.GuideLine_First, ConstraintSet.START);
            constraintSet.connect(Standard_Diagram.getId(), ConstraintSet.END, R.id.GuideLine_Second, ConstraintSet.END);
        }

        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        //Toast.makeText(this, CanvasView.get_score(Objects.requireNonNull(TestName)), Toast.LENGTH_SHORT).show();
        if (which == DialogInterface.BUTTON_POSITIVE) {
            try {
                CanvasView.save(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // OCR
            Ink Ink = ReplicaTranscriptionCanvas.getInk();
            recognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(model).build());
            recognizer.recognize(Ink).addOnSuccessListener(result -> {
                String pred_str = result.getCandidates().get(0).getText();
                //Toast.makeText(this, CanvasView.get_score(TestName), Toast.LENGTH_SHORT).show();
                CanvasView.refreshInk();
                // Time Spent
                int rounded = (int) Math.round(CanvasView.GetTestTime());
                int seconds = ((rounded % 86400) % 3600) % 60;
                int minutes = ((rounded % 86400) % 3600) / 60;
                int hours = ((rounded % 86400) / 3600);
                // Intent
                Intent it = new Intent();
                it.setClass(this, TranscriptionTest.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
                Bundle bundle = new Bundle();
                String time_spent = String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
                switch (Objects.requireNonNull(TestName)) {
                    case "Xie":
                        bundle.putString("Next_TestName", "Liu");
                        bundle.putString("Xie_Pred_Str", pred_str);
                        bundle.putString("Xie_Time", time_spent);
                        bundle.putString("Xie_Strokes", CanvasView.GetStrokesNum());
                        bundle.putString("Xie_SIM", CanvasView.get_score(TestName));
                        break;
                    case "Liu":
                        bundle.putString("Next_TestName", "Gui");
                        bundle.putString("Xie_Pred_Str", getIntent().getExtras().getString("Xie_Pred_Str"));
                        bundle.putString("Xie_Time", getIntent().getExtras().getString("Xie_Time"));
                        bundle.putString("Xie_Strokes", getIntent().getExtras().getString("Xie_Strokes"));
                        bundle.putString("Xie_SIM", getIntent().getExtras().getString("Xie_SIM"));
                        bundle.putString("Liu_Pred_Str", pred_str);
                        bundle.putString("Liu_Time", time_spent);
                        bundle.putString("Liu_Strokes", CanvasView.GetStrokesNum());
                        bundle.putString("Liu_SIM", CanvasView.get_score(TestName));
                        break;
                    case "Gui":
                        bundle.putString("Next_TestName", "Finished");
                        bundle.putString("Xie_Pred_Str", getIntent().getExtras().getString("Xie_Pred_Str"));
                        bundle.putString("Xie_Time", getIntent().getExtras().getString("Xie_Time"));
                        bundle.putString("Xie_Strokes", getIntent().getExtras().getString("Xie_Strokes"));
                        bundle.putString("Xie_SIM", getIntent().getExtras().getString("Xie_SIM"));
                        bundle.putString("Liu_Pred_Str", getIntent().getExtras().getString("Liu_Pred_Str"));
                        bundle.putString("Liu_Time", getIntent().getExtras().getString("Liu_Time"));
                        bundle.putString("Liu_Strokes", getIntent().getExtras().getString("Liu_Strokes"));
                        bundle.putString("Liu_SIM", getIntent().getExtras().getString("Liu_SIM"));
                        bundle.putString("Gui_Pred_Str", pred_str);
                        bundle.putString("Gui_Time", time_spent);
                        bundle.putString("Gui_Strokes", CanvasView.GetStrokesNum());
                        bundle.putString("Gui_SIM", CanvasView.get_score(TestName));
                        break;
                }
                it.putExtras(bundle);
                startActivity(it);
                    }).addOnFailureListener(e ->
                            Log.e("Digital Ink Test", "Error during recognition: " + e));
        }
    }

    // OCR Init
    private void initializeRecognition() throws MlKitException {
        Button DoneButton = (Button) findViewById(R.id.DoneBtn);
        DigitalInkRecognitionModelIdentifier modelIdentifier =
                DigitalInkRecognitionModelIdentifier.fromLanguageTag("ZH_HANI_TW");
        // Use "zh-Hani-CN" for Chinese!
        assert modelIdentifier != null;
        model = DigitalInkRecognitionModel.builder(modelIdentifier).build();
        remoteModelManager.download(model, new DownloadConditions.Builder().build())
                .addOnSuccessListener(aVoid -> {
                    Log.i("InkSample", "Model Downloaded");
                    DoneButton.setEnabled(true);
                })
                .addOnFailureListener(e ->
                        Log.e("InkSample", "Model failed " + e));
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