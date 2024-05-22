package tw.cgu.b0929056.testapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReplicaGraphTrailCanvas extends View {
    Context context;
    private Paint myBitmapPaint;
    private Bitmap myBitmap, ansBitmap, myBackground, answerBackground;
    private Canvas myCanvas;

    // Draw PaintBrush
    private Path myPath;
    private Paint myCirclePaint, myPaint;
    private Path myCirclePath;

    // Temp Finger's (X, Y) Coordinate
    private float myX, myY;
    private static final float TOUCH_TOLERANCE = 4;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
    Date curDate = new Date(System.currentTimeMillis());

    public boolean StartDrawingFlag = false;

    MyDataStore dataStore;

    public ReplicaGraphTrailCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        myPath = new Path();
        myBitmapPaint = new Paint(Paint.DITHER_FLAG);

        // Show circle when the screen is tapped
        myCirclePaint = new Paint();
        myCirclePath = new Path();
        myCirclePaint.setAntiAlias(true);
        myCirclePaint.setColor(Color.BLACK);
        myCirclePaint.setStyle(Paint.Style.STROKE);
        myCirclePaint.setStrokeJoin(Paint.Join.MITER);
        myCirclePaint.setStrokeWidth(4f);

        // Draw Line
        myPaint = new Paint();
        myPaint.setAntiAlias(true);
        myPaint.setDither(true);
        myPaint.setColor(Color.BLACK);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeJoin(Paint.Join.ROUND);
        myPaint.setStrokeCap(Paint.Cap.ROUND);
        myPaint.setStrokeWidth(90);

        if (OpenCVLoader.initDebug()) {
            Log.i("Check","OpenCv configured successfully");
        } else {
            Log.i("Check","OpenCv doesn’t configured successfully");
        }

        dataStore = new MyDataStore(context);
    }

    public Bitmap ImageCompress(Bitmap oldBitmap, int Limit_w, int Limit_h) {
        int srcWidth = oldBitmap.getWidth();
        int srcHeight = oldBitmap.getHeight();
        float heightScale = ((float) Limit_h) / srcHeight;
        float widthScale = heightScale;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale, 0, 0);
        Bitmap newBitmap = Bitmap.createBitmap(Math.round(srcWidth * widthScale), Math.round(srcHeight * heightScale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(oldBitmap, matrix, paint);
        return newBitmap;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Initialize Empty Canvas
        myBitmap = ImageCompress(myBackground, getWidth(), getHeight());
        myCanvas = new Canvas(myBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Background Setting
        @SuppressLint("DrawAllocation")
        Bitmap res = ImageCompress(myBackground, getWidth(), getHeight());
        canvas.drawBitmap(res, 0, 0, myBitmapPaint);

        // Get the Content from Previous Action
        canvas.drawBitmap(myBitmap, 0, 0, myBitmapPaint);
        // Drawing According to the Moving Path
        canvas.drawPath(myPath, myPaint);
        // Drawing Circle
        canvas.drawPath(myCirclePath, myCirclePaint);
    }

    // Override: Detecting User Touch Screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!StartDrawingFlag) {
            float x = event.getX(), y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    StartDrawingFlag = true;
                    break;
            }
            invalidate();
        }
        /*
        float x = event.getX(), y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                break;
        }
        invalidate();
        return true;
         */
        return true;
    }

    // While Touch Screen, get Finger's (X, Y) Position and Set Start Flag
    private void touch_start(float x, float y) {
        myPath.reset();
        myPath.moveTo(x, y);
        myX = x;
        myY = y;
    }

    // While Move Finger, Keep Refreshing Moving Path
    private void touch_move(float x, float y) {
        // Calculate Previous Point and Current Point Difference
        float dx = Math.abs(x - myX), dy = Math.abs(y - myY);
        // Judging the Difference is Tolerance or Not, if Bigger than Tolerance Value then Drawing
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // Drawing Bezier curve
            myPath.quadTo(myX, myY, (x + myX) / 2, (y + myY) / 2);
            // Refreshing Previous (X, Y) Coordinate
            myX = x;
            myY = y;
            // Destroy Previous Circle
            myCirclePath.reset();
            // Refreshing Current Circle's Position
            myCirclePath.addCircle(myX, myY, 30, Path.Direction.CW);
        }
    }

    // While Finger Move out, Set End Flag
    private void touch_up() {
        myPath.lineTo(myX, myY);
        myCirclePath.reset();
        myCanvas.drawPath(myPath, myPaint);
        myPath.reset();
    }

    // Clear PaintBrush Content
    public void clear() {
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
        setDrawingCacheEnabled(true);
        Toast.makeText(this.getContext(), "畫布已成功清除", Toast.LENGTH_SHORT);
    }

    // Set Brush Thickness
    public void setBrushThickness(int thickness) {
        myPaint.setStrokeWidth(thickness);
    }

    // Complete The Test
    public void save(Context context) throws IOException {
        String FilePrefix = "";
        if ((dataStore.getValue(MyDataStore.GRADE_KEY)) != 0) {
            FilePrefix += (getResources().getStringArray(R.array.Grade)[dataStore.getValue(MyDataStore.GRADE_KEY)].toString() + "_");
        }
        if ((dataStore.getValue(MyDataStore.NAME_KEY)).length() > 0) {
            FilePrefix += ((dataStore.getValue(MyDataStore.NAME_KEY)) + "_");
        } else {
            FilePrefix += ("訪客_");
        }
        // Android Q Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
            StorageManager storageManager = (StorageManager) context.getSystemService(context.STORAGE_SERVICE);
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // Internal Storage
            Bitmap bitmapInputImage = myBitmap;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapInputImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesArray = byteArrayOutputStream.toByteArray();
            File fileOutput = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                fileOutput = new File(storageVolume.getDirectory().getPath() + "/Download/" + FilePrefix + formatter.format(curDate) + ".jpg");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
            fileOutputStream.write(bytesArray);

            // Refresh the Gallery
            MediaScannerConnection.scanFile(context,
                    new String[]{fileOutput.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            fileOutputStream.close();
        }
        else // Old Version
        {
            // 檢查權限是否已經授予
            if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 權限未授予，進行申請
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
            } else {
                try {
                    String fileName = FilePrefix + formatter.format(curDate) + ".jpg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    File file = new File(directory, fileName);
                    values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());

                    Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    try (OutputStream output = context.getContentResolver().openOutputStream(uri)) {
                        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    }
                    // Refresh the Gallery
                    MediaScannerConnection.scanFile(context,
                            new String[]{file.toString()}, null,
                            (path, uri1) -> {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri1);
                            });
                } catch (Exception e) {
                    Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
                }
            }
        }
    }

    // Calculate The Test Score
    public String[] get_score() {
        ansBitmap = ImageCompress(answerBackground, getWidth(), getHeight());

        Mat myMat = new Mat();
        Mat ansMat = new Mat();
        Utils.bitmapToMat(myBitmap, myMat);
        Utils.bitmapToMat(ansBitmap, ansMat);
        Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(ansMat, ansMat, Imgproc.COLOR_BGR2GRAY);

        // Calculate IOU
        int ans_Counter = 0;
        int correct_Counter = 0;
        int NOTans_Counter = 0;
        int outsize_Counter = 0;
        for (int i = 0; i < ansMat.height(); i++) {
            for (int j = 0; j < ansMat.width(); j++) {
                double[] ans_pixels = ansMat.get(i, j);
                double[] pixels = myMat.get(i, j);
                // In Path
                if (Math.round(ans_pixels[0]) == 0) {
                    ans_Counter++;
                    if (Math.round(pixels[0]) == Math.round(ans_pixels[0])) {
                        correct_Counter++;
                    }
                } else { // Out Path
                    NOTans_Counter++;
                    if (Math.round(pixels[0]) == 0) {
                        outsize_Counter++;
                    }
                }
            }
        }

        String[] Scores = new String[2];
        Scores[0] = String.valueOf((correct_Counter / (ans_Counter * 1.0)));
        Scores[1] = String.valueOf((outsize_Counter / (NOTans_Counter * 1.0)));

        return Scores;
    }

    // Setting Background Image
    public void setting_background(int id, int id_ans) {
        myBackground = BitmapFactory.decodeResource(getResources(), id);
        answerBackground = BitmapFactory.decodeResource(getResources(), id_ans);
    }
}
