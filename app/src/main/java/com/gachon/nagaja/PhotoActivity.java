//package com.gachon.nagaja;
//
///* PhotoActivity.java */
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//public class PhotoActivity extends AppCompatActivity {
//    final private static String TAG = "GILBOMI";
//    Button btn_photo;
//    ImageView iv_photo;
//
//    final static int TAKE_PICTURE = 1;
//
//    String mCurrentPhotoPath;
//    final static int REQUEST_TAKE_PHOTO = 1;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_photo);
//
//        iv_photo = findViewById(R.id.iv_photo);
//        btn_photo = findViewById(R.id.btn_photo);
//
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "권한 설정 완료");
//            }
//            else {
//                Log.d(TAG, "권한 설정 요청");
//                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
//
//        btn_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btn_photo:
//                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, TAKE_PICTURE);
//                        break;
//                }
//            }
//        });
//    }
//
//
//    // 권한 요청
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d(TAG, "onRequestPermissionsResult");
//        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
//            Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
//        }
//    }
//
//
//    // 카메라로 촬영한 사진의 썸네일을 가져와 이미지뷰에 띄워줌
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//
//        switch (requestCode) {
//            case TAKE_PICTURE:
//                if (resultCode == RESULT_OK && intent.hasExtra("data")) {
//                    Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
//                    if (bitmap != null) {
//                        imageView.setImageBitmap(bitmap);
//                    }
//
//                }
//                break;
//        }
//    }
//}