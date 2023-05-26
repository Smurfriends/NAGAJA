package com.gachon.nagaja;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        String fileName = "bookmarklist.txt";
        String fileContent = "This is a sample bookmark list.";

        try {
            File file = new File(getFilesDir(), fileName);
            if (file.exists()) {
                // File doesn't exist, create it
                FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.write(fileContent.getBytes());
                fos.close();
                Log.d("LoadingActivity", "bookmarklist.txt 파일이 생성되었습니다.");
            } else {
                Log.d("LoadingActivity", "bookmarklist.txt 파일이 이미 존재합니다.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LoadingActivity", "bookmarklist.txt 파일 생성 중 오류가 발생했습니다.");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, ActivityMenu.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }
}
