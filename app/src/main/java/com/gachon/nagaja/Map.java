package com.gachon.nagaja;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class Map extends AppCompatActivity {
    private EditText mEtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mEtAddress = findViewById(R.id.et_address);
        // block touch
        mEtAddress.setFocusable(false);
        mEtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소 검색 웹 뷰 화면으로 이동
                Intent intent = new Intent(Map.this, SearchActivity.class);
                getSearchResult.launch(intent);
            }
        });
    }

    private final ActivityResultLauncher<Intent> getSearchResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // search activity로부터의 결과 값이 이곳으로 전달 된다.. (setResult에 의해)

                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        String data = result.getData().getStringExtra("data");
                        Log.d("Result: ", data);
                        mEtAddress.setText(data);
                    }
                }
            }

    );
}

