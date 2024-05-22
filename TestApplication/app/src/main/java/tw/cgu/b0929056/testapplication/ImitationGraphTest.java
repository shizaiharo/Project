package tw.cgu.b0929056.testapplication;

import static org.tensorflow.lite.schema.TensorType.UINT8;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.ColorSpaceType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import tw.cgu.b0929056.testapplication.ml.PhonetestMetadata;
import tw.cgu.b0929056.testapplication.ml.PhonetestMetadataFc;

public class ImitationGraphTest extends AppCompatActivity
implements DialogInterface.OnClickListener {

    Activity context = this;
    ReplicaImitationCanvas CanvasView;
    String TestName;

    // DataStore
    MyDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imitation_graph_test);

        CanvasView = findViewById(R.id.ImitationGraphCanvasView);

        TestName = getIntent().getExtras().getString("Next_TestName");
        String Guidance_Title;

        ImageView Standard_Diagram = (ImageView) findViewById(R.id.Imitation_STD_Diagram);

        switch (Objects.requireNonNull(TestName)) {
            case "Circle":
                Guidance_Title = "練習題：圓形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.circle);
                break;
            case "Square":
                Guidance_Title = "正式題（一）：正方形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.square);
                break;
            case "Hexagon":
                Guidance_Title = "正式題（二）：六邊形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.hexagon);
                break;
            case "Rhombus":
                Guidance_Title = "正式題（三）：菱形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.rhombus);
                break;
            case "Club":
                Guidance_Title = "正式題（四）：梅花形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.club);
                break;
            case "Olympic":
                Guidance_Title = "正式題（五）：奧運圖形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.olympic);
                break;
            case "MultiRhombus":
                Guidance_Title = "正式題（六）：多菱形仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.multi_rhombus);
                break;
            case "The_3DSquare":
                Guidance_Title = "正式題（七）：立方體仿畫測驗";
                Standard_Diagram.setImageResource(R.drawable.the_3d_square);
                break;
            case "Finished":
                Guidance_Title = null;
                startActivity(new Intent(this, ImitationGraphResult.class));
                break;
            default:
                Guidance_Title = null;
                break;
        }

        AlertDialog.Builder Guidance = new AlertDialog.Builder(this);
        Guidance.setIcon(android.R.drawable.btn_star_big_on)
                .setTitle(Guidance_Title)
                .setMessage("請畫出一模一樣的圖。")
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
                    .setPositiveButton("是", (dialogInterface, i) -> startActivity(new Intent(context, TestInterface.class)))
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

        Button RecBtn = findViewById(R.id.Rec);
        RecBtn.setOnClickListener(view -> {
            try {
                PhonetestMetadata model = PhonetestMetadata.newInstance(this);

                // 假設 grayscaleBitmap 是 ALPHA_8 格式的 Bitmap
                int width = 580;
                int height = 820;

                Bitmap gray = CanvasView.Rec();

                // 創建一個 ByteBuffer 來保存圖像數據
                TensorBuffer inputBuffer = TensorBuffer.createFixedSize(new int[]{1, height, width, 1}, DataType.FLOAT32);
                // 遍历 ALPHA_8 格式的 Bitmap，将每个像素的数据添加到 TensorBuffer 中
                ByteBuffer byteBuffer = ByteBuffer.allocate(4 * width * height);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixelValue = Color.alpha(gray.getPixel(x, y));
                        byteBuffer.put((byte) pixelValue);
                    }
                }

// 将 ByteBuffer 的数据复制到 TensorBuffer 中
                inputBuffer.loadBuffer(byteBuffer);

                TensorImage image = new TensorImage(DataType.FLOAT32);
                image.load(inputBuffer, ColorSpaceType.GRAYSCALE);
//                ImageProcessor processor =
//                        new ImageProcessor.Builder().add(new TransformToGrayscaleOp()).build();
//                image = processor.process(image);
                


                // Creates inputs for reference.
                //TensorImage image = TensorImage.fromBitmap(CanvasView.Rec());
                // Runs model inference and gets result.
//                PhonetestMetadata.Outputs outputs = model.process(image);
//                List<Category> probability = outputs.getProbabilityAsCategoryList();
//
//                int cnt = 0;
//                for (Category element:probability) {
//                    float num = Float.parseFloat(String.valueOf(element.getScore()));
//                    if (num >= 0.5) {
//                        cnt += 1;
//                    }
//                }
//
//                Toast.makeText(this, String.valueOf(cnt), Toast.LENGTH_SHORT).show();
//
//                // Releases model resources if no longer used.
//                model.close();
            } catch (IOException e) {
                // TODO Handle the exception
            }
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
                Intent it = new Intent();
                it.setClass(this, ImitationGraphTest.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
                Bundle bundle = new Bundle();
                switch (Objects.requireNonNull(TestName)) {
                    case "Circle":
                        bundle.putString("Next_TestName", "Square");
                        break;
                    case "Square":
                        bundle.putString("Next_TestName", "Hexagon");
                        break;
                    case "Hexagon":
                        bundle.putString("Next_TestName", "Rhombus");
                        break;
                    case "Rhombus":
                        bundle.putString("Next_TestName", "Club");
                        break;
                    case "Club":
                        bundle.putString("Next_TestName", "Olympic");
                        break;
                    case "Olympic":
                        bundle.putString("Next_TestName", "MultiRhombus");
                        break;
                    case "MultiRhombus":
                        bundle.putString("Next_TestName", "The_3DSquare");
                        break;
                    case "The_3DSquare":
                        bundle.putString("Next_TestName", "Finished");
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