package com.gachon.nagaja;

import androidx.appcompat.app.AppCompatActivity;

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

        uploadImgBtn = findViewById(R.id.uploadImgBtn);
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

    }


    // declaration
    private EditText address;
    private EditText buildingName;
    private EditText floorUndergroundNum;
    private EditText floorUpgroundNum;
    private EditText floorOutsideEntrance;
    private Button uploadImgBtn;
    private ImageView imageView;
    private EditText floorOfMap;
    private Button submitBtn;

}