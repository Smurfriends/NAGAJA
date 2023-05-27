package com.gachon.nagaja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapFragment extends Fragment {
    private EditText mEtAddress;
    Button scanbtn;

    String buildingName;
    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_map, container, false);

        mEtAddress = rootView.findViewById(R.id.et_address);
        // block touch
        mEtAddress.setFocusable(false);
        scanbtn = rootView.findViewById(R.id.scanBtn);

        scanbtn.setEnabled(false);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 검색 웹 뷰 화면으로 이동
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getSearchResult.launch(intent);
            }
        });
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckData(buildingName);
            }
        });

        return rootView;

    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // search activity로부터의 결과 값이 이곳으로 전달 된다.. (setResult에 의해)

                if (result.getResultCode() == getActivity().RESULT_OK) {
                    if (result.getData() != null) {
                        buildingName = result.getData().getStringExtra("data");
                        Log.d("Result: ", buildingName);
                        mEtAddress.setText(buildingName);
                        scanbtn.setEnabled(true);
                    }
                }
            }
    );

    private void CheckData(String buildingName){
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("map").child(buildingName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {// 만약 입력받은 건물 이름이 이미 있을 때.
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.menu_frame_layout, new ImageFragment(buildingName))
                            .addToBackStack(null)
                            .commit();

                } else {// 만약 입력 받은 데이터가 없으면.
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.menu_frame_layout, new UploadMapFragment(buildingName))
                            .addToBackStack(null)
                            .commit();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

}

