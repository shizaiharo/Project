package tw.cgu.b0929056.testapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
implements View.OnClickListener {

    Activity context = this;
    Button Guest_Btn, test, Login_Btn, Register_Btn;
    EditText AccountEditText, PasswordEditText;
    FirebaseAuth uAuth;
    MyDataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AccountEditText = (EditText) findViewById(R.id.AccountEdt);
        PasswordEditText = (EditText) findViewById(R.id.PasswordEdt);
        Guest_Btn = findViewById(R.id.GuestBtn);
        Guest_Btn.setOnClickListener(this);
        Login_Btn = findViewById(R.id.LoginBtn);
        Login_Btn.setOnClickListener(this);
        Register_Btn = findViewById(R.id.RegisterBtn);
        Register_Btn.setOnClickListener(this);

        uAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.LoginBtn) {
            String Account = AccountEditText.getText().toString();
            String Password = PasswordEditText.getText().toString();
            uAuth.signInWithEmailAndPassword(Account, Password).addOnCompleteListener(context, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = uAuth.getCurrentUser();
                    Toast.makeText(context, "結果：" + user.getEmail() + " 登入成功！", Toast.LENGTH_LONG).show();
                    String email = user.getEmail();
                    Intent it = new Intent();
                    it.setClass(this, MainActivity.class);
                    startActivity(it);
                } else {
                    Toast.makeText(context, "結果：登入失敗！", Toast.LENGTH_LONG).show();
                }
            });
        } else if (view.getId() == R.id.RegisterBtn) {
            String Account = AccountEditText.getText().toString();
            String Password = PasswordEditText.getText().toString();
            uAuth.createUserWithEmailAndPassword(Account, Password).addOnCompleteListener(context, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = uAuth.getCurrentUser();
                    Toast.makeText(context, "結果：" + user.getEmail() + " 註冊成功！", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "結果：註冊失敗！", Toast.LENGTH_LONG).show();
                }
            });
        } else if (view.getId() == R.id.GuestBtn) {
            Intent it = new Intent();
            it.setClass(this, MainActivity.class);
            startActivity(it);
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