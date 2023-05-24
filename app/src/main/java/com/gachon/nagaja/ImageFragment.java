package com.gachon.nagaja;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Timer;
import java.util.TimerTask;

public class ImageFragment extends Fragment  {

    ImageView imageView;
    FindPath findPath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = rootView.findViewById(R.id.canvasView);

        //터치시 시작 좌표 받아오는 코드 추가해야함

//        Storage 객체 만들기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://nagaja-3bb34.appspot.com");
        StorageReference storageRef = storage.getReference();

        String bName = "경기도 어쩌구 수정구 저쩌구";
        //파일명 만들기
        String filename = "print" + bName+".jpg";

        //findPath 선언 이거 해야 URL등 firebase에서 값 읽어옴
        FindPath findPath = new FindPath(0,0,bName);
        //URL 저장

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int fireId = Integer.parseInt(findPath.getId());
                // 딜레이 후 실행될 코드 작성
                storageRef.child("image" + fireId + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri)
                                .into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Invalid map URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 1000);

        return rootView;
    }
}