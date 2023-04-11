package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import org.opencv.android.BaseLoaderCallback;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

import org.opencv.android.LoaderCallbackInterface;

import org.opencv.android.OpenCVLoader;

import org.opencv.core.DMatch;
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
import org.opencv.core.MatOfByte;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import org.opencv.core.Core;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Point;
//import org.opencv.imgproc.HoughLinesP;
import org.opencv.core.Scalar;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSION = 2;

    ImageView imageView;
    Button button;
    Button selectImageButton;
    Button detectSubImageButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "Unable to load OpenCV");
        } else {
            Log.d("OpenCV", "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image);
        Bitmap subBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sub_image);

        // Convert the image to grayscale using OpenCV
        Mat fullMat = new Mat();
        Utils.bitmapToMat(imageBitmap, fullMat);

        Mat subMat = new Mat();
        Utils.bitmapToMat(subBitmap, subMat);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        selectImageButton = findViewById(R.id.select_image_button);
        detectSubImageButton = findViewById(R.id.detect_image_button);

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

        detectSubImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                displayOutputImage(findSubImage(fullMat, subMat, true, 0));
            }
        });

    }

    private void convertImageToGrayscale() {
        try {
            // Get the selected image from the ImageView
            ImageView imageView = findViewById(R.id.imageView);
            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            // Convert the image to grayscale using OpenCV
            Mat rgbaMat = new Mat();
            Utils.bitmapToMat(imageBitmap, rgbaMat);

            Mat grayMat = new Mat(rgbaMat.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);

            Mat lineMat = detectLines(rgbaMat, grayMat);

            Bitmap resultBitmap = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(lineMat, resultBitmap);
            imageView.setImageBitmap(resultBitmap);

            Bitmap grayBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(grayMat, grayBitmap);

            imageView.setImageBitmap(grayBitmap);
        } catch (Exception e) {
            Log.e("OpenCV", "Error processing image", e);
        }
    }

//    private void convertImageToGrayscale() {
//        try {
//            // Load the image from the drawable folder
//            Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.data1);
//
//            // Convert the image to grayscale using OpenCV
//            Mat rgbaMat = new Mat();
//            Utils.bitmapToMat(imageBitmap, rgbaMat);
//
//            Mat grayMat = new Mat(rgbaMat.size(), CvType.CV_8UC1);
//            Imgproc.cvtColor(rgbaMat, grayMat, Imgproc.COLOR_RGBA2GRAY);
//
//            Mat lineMat = detectLines(rgbaMat, grayMat);
//
//            Bitmap resultBitmap = Bitmap.createBitmap(rgbaMat.cols(), rgbaMat.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(lineMat, resultBitmap);
//            imageView.setImageBitmap(resultBitmap);
//
//            Bitmap grayBitmap = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(grayMat, grayBitmap);
//
//
//            imageView.setImageBitmap(grayBitmap);
//        } catch (Exception e) {
//            Log.e("OpenCV", "Error processing image", e);
//        }
//    }


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


    public Mat findSubImage(Mat fullImage, Mat subImage, boolean useRGB, double accuracyThreshold) {
        // Convert images to grayscale if required
        if (!useRGB) {
            Imgproc.cvtColor(fullImage, fullImage, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(subImage, subImage, Imgproc.COLOR_BGR2GRAY);
        }

        // Create the AKAZE feature detector and descriptor
        AKAZE akaze = AKAZE.create();

        // Detect keypoints and compute descriptors for both images
        MatOfKeyPoint fullImageKeypoints = new MatOfKeyPoint();
        Mat fullImageDescriptors = new Mat();
        akaze.detectAndCompute(fullImage, new Mat(), fullImageKeypoints, fullImageDescriptors);

        MatOfKeyPoint subImageKeypoints = new MatOfKeyPoint();
        Mat subImageDescriptors = new Mat();
        akaze.detectAndCompute(subImage, new Mat(), subImageKeypoints, subImageDescriptors);

        // Create the descriptor matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        // Match the descriptors
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(subImageDescriptors, fullImageDescriptors, matches);

        // Find good matches based on the accuracy threshold
        List<DMatch> goodMatchesList = new ArrayList<>();
        for (DMatch match : matches.toList()) {
//            if (match.distance <= accuracyThreshold) {
                goodMatchesList.add(match);
//            }
        }

        // Draw the good matches on a new image
        Mat outputImage = new Mat();
        Features2d.drawMatches(subImage, subImageKeypoints, fullImage, fullImageKeypoints,
                new MatOfDMatch(goodMatchesList.toArray(new DMatch[0])),
                outputImage, Scalar.all(-1), Scalar.all(-1), new MatOfByte(), Features2d.DrawMatchesFlags_NOT_DRAW_SINGLE_POINTS);

        return outputImage;
    }

    public void displayOutputImage(Mat outputImage) {
        // Convert the outputImage Mat to a Bitmap
        Bitmap outputBitmap = Bitmap.createBitmap(outputImage.cols(), outputImage.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputImage, outputBitmap);

        // Set the Bitmap as the content of the ImageView
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(outputBitmap);
    }
}