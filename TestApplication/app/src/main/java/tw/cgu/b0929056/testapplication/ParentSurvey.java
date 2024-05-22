package tw.cgu.b0929056.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ParentSurvey extends AppCompatActivity
implements View.OnClickListener {

    TextView Question;

    RadioGroup AnswerGroup;

    RadioButton Ans_1, Ans_2, Ans_3, Ans_4;

    Button Return_Btn, Check_Btn;

    int Current_QuesNum = 0; // 紀錄當前題號

    int[] All_Ans_Array = new int[40]; // 紀錄家長問卷答案（默認為0）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_survey);

        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("家長問卷");
        ImageButton Back_Btn = findViewById(R.id.Bar_BackButton);
        Back_Btn.setOnClickListener(view -> {
            Intent it = new Intent();
            it.setClass(this, TestInterface.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 它可以關掉所到頁面中間的所有Activity
            startActivity(it);
        });

        Question = (TextView) findViewById(R.id.Questions);
        AnswerGroup = (RadioGroup) findViewById(R.id.AnswerRadioGroup);
        Ans_1 = (RadioButton) findViewById(R.id.RadioBtn_Ans1);
        Ans_2 = (RadioButton) findViewById(R.id.RadioBtn_Ans2);
        Ans_3 = (RadioButton) findViewById(R.id.RadioBtn_Ans3);
        Ans_4 = (RadioButton) findViewById(R.id.RadioBtn_Ans4);
        Return_Btn = (Button) findViewById(R.id.ReturnBtn);
        Check_Btn = (Button) findViewById(R.id.CheckBtn);

        Return_Btn.setOnClickListener(this);
        Check_Btn.setOnClickListener(this);

        // 顯示第一道題目的資訊
        Question.setText((Current_QuesNum + 1) + ". " + getResources().getStringArray(R.array.Fine_Motor_Survey_Questions)[0]);
        Ans_1.setText("不佳：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[0]);
        Ans_2.setText("尚可：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[1]);
        Ans_3.setText("良好：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[2]);
        Ans_4.setText("優秀：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[3]);

        // 如果為第一題，返回按鈕不可按
        Return_Btn.setEnabled(false);
    }


    @Override
    public void onClick(View view) {

        int selectId = AnswerGroup.getCheckedRadioButtonId();

        if (view.getId() == R.id.ReturnBtn) {
            Current_QuesNum--;
        } else if (view.getId() == R.id.CheckBtn) {
            if (selectId == -1) {
                Toast.makeText(this, "您尚未選取任何選項！", Toast.LENGTH_SHORT).show();
            } else if (Current_QuesNum == 39) { // 結束測驗
                startActivity(new Intent(this, MainActivity.class));
            } else {
                switch (selectId) {
                    case R.id.RadioBtn_Ans1:
                        All_Ans_Array[Current_QuesNum] = 1;
                        break;
                    case R.id.RadioBtn_Ans2:
                        All_Ans_Array[Current_QuesNum] = 2;
                        break;
                    case R.id.RadioBtn_Ans3:
                        All_Ans_Array[Current_QuesNum] = 3;
                        break;
                    case R.id.RadioBtn_Ans4:
                        All_Ans_Array[Current_QuesNum] = 4;
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + selectId);
                }

                Current_QuesNum++;
            }
        }

        // 顯示下一道題目的資訊
        if (Current_QuesNum < 20) { // 題號小於等於 20 即為精細動作的家長問卷內容
            Return_Btn.setEnabled(Current_QuesNum != 0); // 如果為第一題，返回按鈕不可按
            Question.setText((Current_QuesNum + 1) + ". " + getResources().getStringArray(R.array.Fine_Motor_Survey_Questions)[Current_QuesNum]);
            Ans_1.setText("不佳：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[4 * Current_QuesNum]);
            Ans_2.setText("尚可：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[4 * Current_QuesNum + 1]);
            Ans_3.setText("良好：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[4 * Current_QuesNum + 2]);
            Ans_4.setText("優秀：" + getResources().getStringArray(R.array.Fine_Motor_Survey_Answers)[4 * Current_QuesNum + 3]);
        } else { // 題號大於 20 即為粗大動作的家長問卷內容
            if (Current_QuesNum == 39) { // 確認按鈕的文字改變
                Check_Btn.setText("完成");
            } else {
                Check_Btn.setText("下一題");
            }
            Question.setText((Current_QuesNum + 1) + ". " + getResources().getStringArray(R.array.Gross_Motor_Survey_Questions)[Current_QuesNum - 20]);
            Ans_1.setText("不佳：" + getResources().getStringArray(R.array.Gross_Motor_Survey_Answers)[4 * (Current_QuesNum - 20)]);
            Ans_2.setText("尚可：" + getResources().getStringArray(R.array.Gross_Motor_Survey_Answers)[4 * (Current_QuesNum - 20) + 1]);
            Ans_3.setText("良好：" + getResources().getStringArray(R.array.Gross_Motor_Survey_Answers)[4 * (Current_QuesNum - 20) + 2]);
            Ans_4.setText("優秀：" + getResources().getStringArray(R.array.Gross_Motor_Survey_Answers)[4 * (Current_QuesNum - 20) + 3]);
        }

        // 答案選項是否要勾選
        if (All_Ans_Array[Current_QuesNum] == 0) { // 尚未作答
            AnswerGroup.clearCheck();
        } else { // 已經作答，顯示其紀錄
            switch (All_Ans_Array[Current_QuesNum]) {
                case 1:
                    AnswerGroup.check(R.id.RadioBtn_Ans1);
                    break;
                case 2:
                    AnswerGroup.check(R.id.RadioBtn_Ans2);
                    break;
                case 3:
                    AnswerGroup.check(R.id.RadioBtn_Ans3);
                    break;
                case 4:
                    AnswerGroup.check(R.id.RadioBtn_Ans4);
                    break;
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