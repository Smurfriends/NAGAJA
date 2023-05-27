package com.gachon.nagaja;

import static android.app.Activity.RESULT_OK;

import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadMapFragment extends Fragment {

//    private FirebaseAuth mAuth;   // 권한 추가 예정
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_upload, container, false);

        address = rootView.findViewById(R.id.address);
        buildingName = rootView.findViewById(R.id.buildingName);
//        floorUpgroundNum = rootView.findViewById(R.id.floorNum);
//        floorOutsideEntrance = rootView.findViewById(R.id.floorOutsideEntrance);

        cameraBtn = rootView.findViewById(R.id.cameraBtn);
        selectImgBtn = rootView.findViewById(R.id.selectImgBtn);
        imageView = rootView.findViewById(R.id.imageView);
        floorOfMap = rootView.findViewById(R.id.floorOfMap);
        submitBtn = rootView.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().equals(""))
                    return;

                // upload building data
                String floorNum = floorOfMap.getText().toString();
                String upgroundNum = floorUpgroundNum.getText().toString();

                BuildingDTO building = new BuildingDTO(buildingName.getText().toString(), Integer.parseInt(floorNum));

                databaseReference.child("building").child(address.getText().toString()).setValue(building);

                // upload map data
                if (imageUri != null) { // 파일에서 선택했다면
                    uploadToFirebase(imageUri);
                }
                else {
                    // 카메라로 찍어서 올린 map이미지 업로드는 아직 구현 못함
                     Toast.makeText(getActivity(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
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

        return rootView;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        Toast.makeText(getActivity(), "업로드 성공.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireContext().getContentResolver();
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