package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import org.opencv.android.OpenCVLoader;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import org.opencv.core.Core;
import org.opencv.core.MatOfInt4;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        selectImageButton = findViewById(R.id.select_image_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainFunction();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

    }
    private void mainFunction() {
        // Get the current image in the ImageView
        Drawable drawable = imageView.getDrawable();

        if (drawable == null) {
            // Show a Toast message indicating that there is no current image
            Toast.makeText(this, "There is no current image.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the drawable to a Bitmap object
        Bitmap currentImage = ((BitmapDrawable) drawable).getBitmap();

        // Process the image using the ImageProcessor class
        Bitmap outputImage = ImageProcessor.processImage(currentImage);

        // Display the output image in an ImageView
        imageView.setImageBitmap(outputImage);
    }


    private void selectImageFromGallery() {// Select Image
        Intent pickImageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        pickImageIntent.setType("image/*");
        startActivityForResult(pickImageIntent, REQUEST_CODE_PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // Get the URI of the selected image
            Uri uri = data.getData();

            // Set the image in ImageView
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageURI(uri);
        }
    }


}