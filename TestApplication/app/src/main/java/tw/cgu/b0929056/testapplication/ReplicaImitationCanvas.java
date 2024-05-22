package tw.cgu.b0929056.testapplication;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8U;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaScannerConnection;
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
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import tw.cgu.b0929056.testapplication.ml.PhonetestMetadata;

public class ReplicaImitationCanvas extends View {
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

    MyDataStore dataStore;

    public ReplicaImitationCanvas(Context context, @Nullable AttributeSet attrs) {
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
        myPaint.setStrokeWidth(10);

        myBackground = BitmapFactory.decodeResource(getResources(), R.drawable.emptycanvas);

        if (OpenCVLoader.initDebug()) {
            Log.i("Check","OpenCv configured successfully");
        } else {
            Log.i("Check","OpenCv doesn’t configured successfully");
        }

        dataStore = new MyDataStore(context);
    }

    public Bitmap ImageCompress(Bitmap oldBitmap, int Limit_w, int Limit_h) {
        /*
        int srcWidth = oldBitmap.getWidth();
        int srcHeight = oldBitmap.getHeight();
        Log.i("Bitmap", "原始圖片比例: " + srcWidth + " * " + srcHeight);
        float widthScale = Limit_w * 1.0f / srcWidth;
        float heightScale = Limit_h * 1.0f / srcHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale, 0, 0);
        Bitmap newBitmap = Bitmap.createBitmap(Limit_w, Limit_h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(oldBitmap, matrix, paint);
        Log.i("Bitmap", "縮放圖片比例: " + Limit_w + " * " + Limit_h);
        return newBitmap;
         */
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
                    //values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/");
                    //values.put(MediaStore.MediaColumns.IS_PENDING, 1);
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
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                } catch (Exception e) {
                    Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
                }
            }
        }
    }

    public String get_score(String TestName) {
        switch (Objects.requireNonNull(TestName)) {
            case "Circle":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
            case "Square":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.square);
            case "Hexagon":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.hexagon);
            case "Rhombus":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.rhombus);
            case "Club":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.club);
            case "Olympic":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.olympic);
            case "MultiRhombus":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.multi_rhombus);
            case "The_3DSquare":
                answerBackground = BitmapFactory.decodeResource(getResources(), R.drawable.the_3d_square);
        }
        ansBitmap = ImageCompress(answerBackground, getWidth(), getHeight());
        Mat myMat = new Mat();
        Mat ansMat = new Mat();
        Utils.bitmapToMat(myBitmap, myMat);
        Utils.bitmapToMat(ansBitmap, ansMat);
        Imgproc.cvtColor(myMat, myMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(ansMat, ansMat, Imgproc.COLOR_BGR2GRAY);
        myMat.convertTo(myMat, CV_32F);
        ansMat.convertTo(ansMat, CV_32F);
        double score = Imgproc.compareHist(myMat, ansMat, Imgproc.CV_COMP_CORREL);

        return Double.toString(score) + "\n";
    }

    public Bitmap ConvertToGrayscale(Bitmap bitmap) {
        int height = super.getHeight();
        int width = super.getWidth();

        float[] arrayForColorMatrix = new float[] {0, 0, 1, 0, 0,
                                                    0, 0, 1, 0, 0,
                                                    0, 0, 1, 0, 0,
                                                    0, 0, 0, 1, 0};

        Bitmap.Config config = bitmap.getConfig();
        Bitmap grayScaleBitmap = Bitmap.createBitmap(width, height, config);

        Canvas c = new Canvas(grayScaleBitmap);
        Paint paint = new Paint();

        ColorMatrix matrix = new ColorMatrix(arrayForColorMatrix);
        matrix.setSaturation(0);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        paint.setColorFilter(filter);

        c.drawBitmap(bitmap, 0, 0, paint);

        return grayScaleBitmap;
    }

    public Bitmap gray(Bitmap bitmap) {
        Bitmap graymap =  Bitmap.createBitmap(580, 820, Bitmap.Config.ALPHA_8);
        for (int x = 0; x < 580; x++) {
            for (int y = 0; y < 820; y++) {
                int pixel = bitmap.getPixel(x, y);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                int grayscale = (int) (0.2989 * red + 0.5870 * green + 0.1140 * blue);
                graymap.setPixel(x, y, grayscale / 255);
            }
        }
        return graymap;
    }

    public Bitmap Rec() {
        Bitmap bitmap = Bitmap.createBitmap(580, 820, Bitmap.Config.ARGB_8888);
        // Creates inputs for reference.
        Mat mat = new Mat(myBitmap.getWidth(), myBitmap.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(myBitmap, mat, true);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
        Size size = new Size(580, 820);
        Imgproc.resize(mat, mat, size);

        Utils.matToBitmap(mat, bitmap);
        return gray(bitmap);
    }
}