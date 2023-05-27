package com.gachon.nagaja;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import org.opencv.android.OpenCVLoader;


import android.graphics.Bitmap;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    final static int TAKE_PICTURE = 2;

    ImageView imageView;
    Button button;
    Button selectImageButton;
    Button cameraButton;

    //메인 화면들 불러오기-UI
    homeFragment homeFragment;
    bookmarkFragment bookmarkFragment;
    uploadFragment uploadFragment;
    storeFragment storeFragment;


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


        // 연동 잘 됐는지 테스트
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Success!");   // 다른 말로 바꿔서 테스트 해보기
        // 현재는 파이어베이스 규칙 탭에서 권한을 풀어 둔 상태. 나중엔 권한 코드도 넣어야 함.

        imageView = findViewById(R.id.imageView);
        //Button
        button = findViewById(R.id.button);
        selectImageButton = findViewById(R.id.select_image_button);
        cameraButton = findViewById(R.id.pic_camera);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 설정 완료");
            }
            else {
                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
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

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureImageByCamera();
            }
        });


        homeFragment = new homeFragment();
        bookmarkFragment = new bookmarkFragment();
        uploadFragment = new uploadFragment();
        storeFragment = new storeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commit();

        NavigationBarView navigationBarView = findViewById(R.id.menu_bottom_bar);

        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.menu_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, homeFragment).commit();
                        return true;
                    case R.id.menu_bookmark:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, bookmarkFragment).commit();
                        return true;
                    case R.id.menu_upload:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, uploadFragment).commit();
                        return true;
                    case R.id.menu_store:
                        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_layout, storeFragment).commit();
                        return true;
                }
                return false;
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

    private void pictureImageByCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == RESULT_OK && data.hasExtra("data")) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }

                }
                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    // Set the image in ImageView
                    imageView.setImageURI(uri);

                }
                break;

        }
    }


}