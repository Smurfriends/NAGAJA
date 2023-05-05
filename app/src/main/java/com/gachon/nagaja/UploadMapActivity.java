package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadMapActivity extends AppCompatActivity {

//    private FirebaseAuth mAuth;   // 권한 추가 예정
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_map);

        address = findViewById(R.id.address);
        buildingName = findViewById(R.id.buildingName);
        floorUndergroundNum = findViewById(R.id.floorUndergroundNum);
        floorUpgroundNum = findViewById(R.id.floorUpgroundNum);
        floorOutsideEntrance = findViewById(R.id.floorOutsideEntrance);

        cameraBtn = findViewById(R.id.cameraBtn);
        selectImgBtn = findViewById(R.id.selectImgBtn);
        imageView = findViewById(R.id.imageView);
        floorOfMap = findViewById(R.id.floorOfMap);
        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().equals(""))
                    return;

                String undergroundNum = floorUndergroundNum.getText().toString();
                String upgroundNum = floorUpgroundNum.getText().toString();

                BuildingDTO building = new BuildingDTO(buildingName.getText().toString(),
                        Integer.parseInt(undergroundNum), Integer.parseInt(upgroundNum));
                databaseReference.child("building").child(address.getText().toString()).setValue(building);
                // TODO: then back to main

            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureImageByCamera();
            }
        });

        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });

    }

    private void pictureImageByCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, TAKE_PICTURE);
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


    // declaration
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int TAKE_PICTURE = 2;
    private EditText address;
    private EditText buildingName;
    private EditText floorUndergroundNum;
    private EditText floorUpgroundNum;
    private EditText floorOutsideEntrance;
    private Button cameraBtn;
    private Button selectImgBtn;
    private ImageView imageView;
    private EditText floorOfMap;
    private Button submitBtn;

}