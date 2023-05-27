package com.gachon.nagaja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

public class MapFragment extends Fragment {
    private EditText mEtAddress;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_map, container, false);

        mEtAddress = rootView.findViewById(R.id.et_address);
        // block touch
        mEtAddress.setFocusable(false);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 검색 웹 뷰 화면으로 이동
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                getSearchResult.launch(intent);
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
                        String data = result.getData().getStringExtra("data");
                        Log.d("Result: ", data);
                        mEtAddress.setText(data);
                    }
                }
            }
    );
}

