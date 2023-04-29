package com.gachon.nagaja;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Size;

public class ImageProcessor {
    public static Bitmap processImage(Bitmap inputImage) {
        // Convert input image to OpenCV Mat object
        Mat inputMat = new Mat();
        Utils.bitmapToMat(inputImage, inputMat);

        // Perform image processing using OpenCV
        Mat outputMat = detectRooms(inputMat);

        // Convert output Mat object to bitmap
        Bitmap outputImage = Bitmap.createBitmap(outputMat.cols(), outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, outputImage);

        return outputImage;
    }

    private static Mat detectRooms(Mat inputMat) {
        // Convert input image to grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(inputMat, grayMat, Imgproc.COLOR_BGR2GRAY);

        // Binarize the image using Otsu's thresholding method
        Mat im_bw = new Mat();
        Imgproc.threshold(grayMat, im_bw, 30, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

        // Perform connected component analysis to obtain all the individual components
        Mat labels = new Mat();
        Mat stats = new Mat();
        Mat centroids = new Mat();
        int nb_components = Imgproc.connectedComponentsWithStats(im_bw, labels, stats, centroids);

        // Filter the components based on their size
        Mat img2 = new Mat(im_bw.size(), CvType.CV_8UC1, new Scalar(0));
        int min_size = 150;
        for (int i = 1; i < nb_components; i++) {
            if (stats.get(i, Imgproc.CC_STAT_AREA)[0] >= min_size) {
                Core.compare(labels, new Scalar(i), img2, Core.CMP_EQ);
                Imgproc.threshold(img2, img2, 0, 255, Imgproc.THRESH_BINARY);
            }
        }

        // Apply morphological transformations to fill gaps between the rooms
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat dilation = new Mat();
        Imgproc.dilate(img2, dilation, kernel);
        Mat erosion = new Mat();
        Imgproc.erode(dilation, erosion, kernel, new Point(-1, -1), 6);

        // Apply morphological transformation to close gaps between the rooms
        Mat closing = new Mat();
        Imgproc.morphologyEx(erosion, closing, Imgproc.MORPH_CLOSE, kernel);

        return closing;
    }

}
