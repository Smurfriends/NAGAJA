package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import org.opencv.android.BaseLoaderCallback;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import org.opencv.android.LoaderCallbackInterface;

import org.opencv.android.OpenCVLoader;

import org.opencv.core.Mat;

import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.provider.MediaStore;
import android.view.SurfaceView;

import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.InputStream;

import androidx.annotation.Nullable;
import org.opencv.core.Core;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Point;
//import org.opencv.imgproc.HoughLinesP;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 2;

    ImageView imageView;
    Button button;
    Button selectImageButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV");
        } else {
            Log.d("OpenCV", "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        selectImageButton = findViewById(R.id.select_image_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertImageToGrayscale();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
    }

    private void convertImageToGrayscale() {
        try {
            // Load the image from the drawable folder
            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image);

            // Convert the image to grayscale using OpenCV
            Mat rgbaMat = new Mat();
            Utils.bitmapToMat(imageBitmap, rgbaMat);

            Mat grayMat = new Mat(rgbaMat.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);

            Mat lineMat = detectLines(rgbaMat, grayMat);

            Bitmap resultBitmap = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(lineMat, resultBitmap);
            imageView.setImageBitmap(resultBitmap);
//
//            Bitmap grayBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(grayMat, grayBitmap);
//
//
//            imageView.setImageBitmap(grayBitmap);
        } catch (Exception e) {
            Log.e("OpenCV", "Error processing image", e);
        }
    }


    private Mat detectLines(Mat mapMat, Mat grayMat){
        Mat edgesMat = new Mat();

        Imgproc.Canny(grayMat, edgesMat, 100, 200);

        MatOfInt4 lines = new MatOfInt4();
        Imgproc.HoughLinesP(edgesMat, lines, 1, Math.PI / 180, 100, 100, 10);


        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0], y1 = line[1], x2 = line[2], y2 = line[3];
            Imgproc.line(mapMat, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0), 2);
        }

        return mapMat;

    }

    private void selectImageFromGallery() {
        Intent pickImageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}