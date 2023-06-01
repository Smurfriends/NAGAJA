package com.gachon.nagaja;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadMapFragment extends Fragment {

    private final String bname;
    //    private FirebaseAuth mAuth;   // 권한 추가 예정
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private int fileId;
    String mCurrentPhotoPath;

    private Activity activity;


    public UploadMapFragment(String buildingName) {
        this.bname = buildingName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_upload, container, false);

        address = rootView.findViewById(R.id.address);
//        buildingName = rootView.findViewById(R.id.buildingName);
//        floorUpgroundNum = rootView.findViewById(R.id.floorNum);
//        floorOutsideEntrance = rootView.findViewById(R.id.floorOutsideEntrance);

        cameraBtn = rootView.findViewById(R.id.cameraBtn);
        selectImgBtn = rootView.findViewById(R.id.selectImgBtn);
        imageView = rootView.findViewById(R.id.imageView);
        floorOfMap = rootView.findViewById(R.id.floorOfMap);
        submitBtn = rootView.findViewById(R.id.submitBtn);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "권한 설정 완료");
            }
            else {
//                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().equals(""))
                    return;

                // upload building data
                String floorNum = floorOfMap.getText().toString();
//                String upgroundNum = floorUpgroundNum.getText().toString();

                BuildingDTO building = new BuildingDTO(address.getText().toString(), Integer.parseInt(floorNum));

                databaseReference.child("building").child(bname).setValue(building);

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

            if(cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
                File photoFile = null;

                try { photoFile = createImageFile(); }
                catch (IOException ex) { }
                if(photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getContext(), "com.gachon.nagaja.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, TAKE_PICTURE);
                }
            }

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
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                    if (bitmap != null) {
//                        imageView.setImageBitmap(bitmap);
//                        imageUri = null;    // initialize imageUri
//                    }
                    File file = new File(mCurrentPhotoPath);
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), Uri.fromFile(file));
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    private void uploadToFirebase(Uri imgUri) {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("map");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    fileId = 0;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Loop through each child node and increment count
                        fileId++;
                    }
                    // Use the count value as needed
                    Log.d("Data Count", String.valueOf(fileId));

                    StorageReference fileRef = reference.child("image" + fileId + ".png");

                    fileRef.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 이미지 모델에 담기
                                    MapDTO newMap = new MapDTO(fileId, uri.toString());

                                    // 데이터 넣기
                                    databaseReference.child("map").child(bname).setValue(newMap);
                                    // 층별 구분 넣기 필요
                                    Toast.makeText(getActivity(), "업로드 성공.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    // Handle the case when there are no data nodes
                    Log.d("Data Count", "No data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }


    //파일타입 가져오기
    private String getFileExtension(Uri uri) {
        ContentResolver cr = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
    }

    // 사진 촬영 후 썸네일만 띄워줌. 이미지를 파일로 저장해야 함
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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