package com.gachon.nagaja;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class ImageFragment extends Fragment  {

    ImageView imageView;
    FindPath findPath;

    Button downloadButton;

    int check;
    int fireId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = rootView.findViewById(R.id.canvasView);
        downloadButton = rootView.findViewById(R.id.downloadButton);

        check = 0;

        //터치시 시작 좌표 받아오는 코드 추가해야함

//        Storage 객체 만들기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://nagaja-3bb34.appspot.com");
        StorageReference storageRef = storage.getReference();

        String bName = "경기도 어쩌구 수정구 저쩌구";
        //파일명 만들기

        //findPath 선언 이거 해야 URL등 firebase에서 값 읽어옴
        FindPath findPath = new FindPath(bName);
        //URL 저장

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireId = Integer.parseInt(findPath.getId());
                // 딜레이 후 실행될 코드 작성
                storageRef.child("image" + fireId + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .load(uri)
                                .into(imageView);
                        check = 1;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Invalid map URL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 1000);

        File fileDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/map_img");

        if(!fileDir.isDirectory()){
            fileDir.mkdir();
        }
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check == 0){
                    Toast.makeText(getActivity(),"ImageFail",Toast.LENGTH_SHORT).show();
                }
                else{
                    String filename = "print_" + bName+".jpg";
                    File downloadFile = new File(fileDir,filename);
                    StorageReference downLoadRef = storageRef.child("image"+fireId+".png");

                    downLoadRef.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("onSuccess",downloadFile.getPath());
                            Toast.makeText(getActivity(),"Download success",Toast.LENGTH_SHORT).show();

                            String buildingName = findPath.getBuildingName();
                            String floorNum = findPath.getFloorNum();
                            String nodeNum = findPath.getNodeNum();
                            String x = findPath.getX();
                            String y = findPath.getY();
                            String id = findPath.getId();
                            String node = findPath.getNode();

                            // 내부 저장소의 bookmarklist.txt 파일 업데이트
                            updateBookmarkList(buildingName, floorNum,nodeNum,x,y,id,node);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("onFailure",downloadFile.getPath());
                        }
                    });
                }

            }
        });
        return rootView;
    }

    private void updateBookmarkList(String buildingName, String floorNum,String nodeNum,String x, String y, String id, String node) {
        String fileName = "bookmarklist.txt";

        try {
            // 내부 저장소에 기존 내용 불러오기
            FileInputStream fis = getActivity().openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
            reader.close();

            // 기존 내용에 추가 정보 붙이기
            content.append("?\n");
            content.append("buildingName: ").append(buildingName).append("\n");
            content.append("floorNum: ").append(floorNum).append("\n");
            content.append("nodeNum: ").append(nodeNum).append("\n");
            content.append("x: ").append(x).append("\n");
            content.append("y: ").append(y).append("\n");
            content.append("ImgURL: ").append(id).append("\n");
            content.append("node: ").append(node).append("\n");

            // 내부 저장소에 업데이트된 내용 저장
            FileOutputStream fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.toString().getBytes());
            fos.close();

            Log.d("LoadingActivity", "bookmarklist.txt 파일이 업데이트되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("LoadingActivity", "bookmarklist.txt 파일 업데이트 중 오류가 발생했습니다.");
        }
    }

}