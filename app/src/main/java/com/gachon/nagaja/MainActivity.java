package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import org.opencv.android.BaseLoaderCallback;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import org.opencv.android.LoaderCallbackInterface;

import org.opencv.android.OpenCVLoader;

import org.opencv.core.Mat;

import org.opencv.android.CameraBridgeViewBase;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.view.SurfaceView;

import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;

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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertImageToGrayscale();
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

            Bitmap grayBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(grayMat, grayBitmap);

            imageView.setImageBitmap(grayBitmap);
        } catch (Exception e) {
            Log.e("OpenCV", "Error processing image", e);
        }
    }

}