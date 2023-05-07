package com.gachon.nagaja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadMapActivity extends AppCompatActivity {

//    private FirebaseAuth mAuth;   // 권한 추가 예정
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

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

                // upload building data
                String undergroundNum = floorUndergroundNum.getText().toString();
                String upgroundNum = floorUpgroundNum.getText().toString();

                BuildingDTO building = new BuildingDTO(buildingName.getText().toString(),
                        Integer.parseInt(undergroundNum), Integer.parseInt(upgroundNum));
                databaseReference.child("building").child(address.getText().toString()).setValue(building);

                // upload map data
                if (imageUri != null) { // 파일에서 선택했다면
                    uploadToFirebase(imageUri);
                }
                else {
                    // 카메라로 찍어서 올린 map이미지 업로드는 아직 구현 못함
                    Toast.makeText(UploadMapActivity.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

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
                        imageUri = null;    // initialize imageUri
                    }

                }
                break;
            case REQUEST_CODE_PICK_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    // Set the image in ImageView
                    imageView.setImageURI(imageUri);

                }
                break;

        }
    }

    //파이어베이스 이미지 업로드
    private void uploadToFirebase(Uri imgUri) {

        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(imgUri));

        fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        //이미지 모델에 담기
                        MapDTO newMap = new MapDTO(uri.toString());

                        //데이터 넣기
                        databaseReference.child("map").child(address.getText().toString()).setValue(newMap);
                        // 층별 구분 넣기 필요
                        Toast.makeText(UploadMapActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
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
    private Uri imageUri;

}