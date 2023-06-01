package com.gachon.nagaja;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.ByteArrayOutputStream;

public class UploadMapFragment extends Fragment {

    private final String bname;
    //    private FirebaseAuth mAuth;   // 권한 추가 예정
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    //private final DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private int fileId;
    private Bitmap imageToUpload;
    byte[] bytesToUploadImage;
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
            if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "권한 설정 완료");
            }
            else {
//                Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 1);
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

                // 홈 화면으로 돌아가기
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, new MapFragment())
                        .addToBackStack(null)
                        .commit();
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
                    transformateImageBitmap();
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

                    fileRef.putBytes(bytesToUploadImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // 이미지 모델에 담기
                                    MapDTO newMap = new MapDTO(fileId, uri.toString());

                                    // 데이터 넣기
                                    databaseReference.child("map").child(bname).setValue(newMap);
                                    Toast.makeText(getActivity(), "업로드 성공", Toast.LENGTH_SHORT).show();
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

    // 720*1200 사이즈로 이미지 변환
    private void transformateImageBitmap() {
//        Bitmap temp = BitmapFactory.decodeResource(imageView.getForeground());
        Bitmap temp = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

        // 긴 쪽이 세로로 보이도록 회전
        if (temp.getWidth() > temp.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            temp = Bitmap.createBitmap(temp, 0, 0,
                    temp.getWidth(), temp.getHeight(), matrix, true);

        }

        // 720*1200으로 통일해서 scale 조정 (화면엔 360dp*600dp로 띄워질 예정)
        float widthScale = temp.getWidth() / 720f;  // float 잊지 말기
        float heightScale = temp.getHeight() / 1200f;

        Matrix matrix = new Matrix();
        if (widthScale > heightScale) {
            matrix.preScale(1 / widthScale, 1 / widthScale);
        } else {
            matrix.preScale(1 / heightScale, 1 / heightScale);
        }
        temp = Bitmap.createBitmap(temp, 0, 0,
                temp.getWidth(), temp.getHeight(), matrix, true);

        Log.d("Resource", "width: " + temp.getWidth() + ", height: " + temp.getHeight());

        // 720*1200 화면에서 딱 맞지 않는 부분은 투명으로 채우기
        Bitmap result = Bitmap.createBitmap(720, 1200, Bitmap.Config.ARGB_8888); // 기본이 투명
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        if (widthScale > heightScale) {
            canvas.drawBitmap(temp, 0, (1200 - temp.getHeight()) / 2f, paint);
        } else {
            canvas.drawBitmap(temp, (720 - temp.getWidth()) / 2f, 0, paint);
        }

        // 데이터베이스에 올릴 bitmap 저장
        imageToUpload = result;

        // imageView에 띄우기
        imageView.setImageBitmap(imageToUpload);

        // 비트맵을 ByteArray로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageToUpload.compress(Bitmap.CompressFormat.PNG, 100, baos);   // 퀄리티 100이면 원본

        // bytesToUploadImage가 파이어베이스 스토리지에 올라감
        bytesToUploadImage = baos.toByteArray();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity)
            activity = (Activity) context;
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