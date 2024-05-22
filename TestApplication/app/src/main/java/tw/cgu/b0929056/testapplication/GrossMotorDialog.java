package tw.cgu.b0929056.testapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GrossMotorDialog extends AppCompatDialogFragment {

    GrossMotorDialog context = this;

    Spinner Gross_Motor;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    Date curDate = new Date(System.currentTimeMillis());

    MyDataStore dataStore;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.gross_motor_dialog, null);
        dataStore = new MyDataStore(getActivity());
        Gross_Motor = view.findViewById(R.id.GrossMotorSpinner);

        builder.setView(view)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int Gross_Motor_Index = Gross_Motor.getSelectedItemPosition();
                        Intent it = new Intent();
                        it.setClass(getContext(), MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Start_Recording", "True");
                        bundle.putInt("Index", Gross_Motor_Index);
                        it.putExtras(bundle);
                        startActivity(it);
                        /*
                        int Gross_Motor_Index = Gross_Motor.getSelectedItemPosition();
                        Intent it = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        it.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, "GM" + (Gross_Motor_Index + 1) + "_" + getResources().getStringArray(R.array.Grade)[dataStore.getValue(MyDataStore.GRADE_KEY)] + "_" + dataStore.getValue(MyDataStore.NAME_KEY) + "_" + formatter.format(curDate) + ".mp4");
                        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                        Uri videoUri = requireContext().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                        it.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        Toast.makeText(getContext(), videoUri.toString(), Toast.LENGTH_SHORT).show();
                        startActivity(it);
                         */
                    }
                })
                .setNegativeButton("取消", null);

        return builder.create();
    }
}