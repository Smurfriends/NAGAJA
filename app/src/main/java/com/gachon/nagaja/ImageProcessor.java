package com.gachon.nagaja;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageProcessor {// 안씀 지울 예정
    public static Bitmap processImage(Bitmap inputImage) {
        // Convert input image to OpenCV Mat object
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputImage, inputMat);

        // Convert to grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // Apply Canny edge detection
        Mat edges = new Mat();
        Imgproc.Canny(grayMat, edges, 50, 150);

        // Convert to grayscale
        Mat outputMat = new Mat();
        Imgproc.cvtColor(edges, outputMat, Imgproc.COLOR_GRAY2RGBA);

        // Convert output Mat object to bitmap
        Bitmap outputImage = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, outputImage);

        return outputImage;
    }

    public static void detectExitSign(String imagePath) {
        Mat src = Imgcodecs.imread(imagePath);

        if (src.empty()) {
            System.err.println("Image not found!");
            return;
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat result = src.clone();
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if (isExitSign(rect)) {
                Imgproc.rectangle(result, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
            }
        }

        Imgcodecs.imwrite("path/to/output.jpg", result);
        System.out.println("Exit sign detection complete.");
    }


    private static boolean isExitSign(Rect rect) {
        double aspectRatio = (double) rect.width / rect.height;
        return aspectRatio >= 1.0 && aspectRatio <= 2.0;
    }
}
