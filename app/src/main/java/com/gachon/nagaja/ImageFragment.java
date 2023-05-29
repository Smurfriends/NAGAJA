package com.gachon.nagaja;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
    FrameLayout frameLayout;
    RouteCanvasView routeCanvasView;
    FindPath findPath;
    Button downloadButton;
    Button adjustNodeButton;

    int check;
    int fileId;
    Bitmap bitmap = null;
    String bname;

    public ImageFragment(String buildingName){
        this.bname = buildingName;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        frameLayout = rootView.findViewById(R.id.frameLayout);
        downloadButton = rootView.findViewById(R.id.downloadButton);
        adjustNodeButton = rootView.findViewById(R.id.adjustNodeButton);
        check = 0;

        // Storage 객체 만들기
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://nagaja-3bb34.appspot.com");
        StorageReference storageRef = storage.getReference();

//        bname = "경기도 어쩌구 수정구 저쩌구";
        //파일명 만들기

        //findPath 선언 이거 해야 URL등 firebase에서 값 읽어옴
        FindPath findPath = new FindPath(bname);
        //URL 저장

        // TODO: 파베에서 좌표 정보 받아오는 코드
        // 받아 온 좌표를 CanvasView에 있는 node_corner ArrayList에 넣기
        routeCanvasView.node = findPath.getNodeArrayList();
        routeCanvasView.matrix.add(findPath.getMatrix());

        // 파이어베이스에서 이미지 가져오기. scale 조정은 나중에 빼기.
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fileId = Integer.parseInt(findPath.getId());
                // 딜레이 후 실행될 코드 작성
                storageRef.child("image" + fileId + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(getActivity())
                                .asBitmap()
                                .load(uri)
                                .into(new SimpleTarget<Bitmap>() {
                                          @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
                                          @Override
                                          public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                              bitmap = resource;
                                              check = 1;

                                              // 긴 쪽이 세로로 보이도록 회전
                                              if (resource.getWidth() > resource.getHeight()) {
                                                  Matrix matrix = new Matrix();
                                                  matrix.postRotate(90);
                                                  resource = Bitmap.createBitmap(resource, 0, 0,
                                                          resource.getWidth(), resource.getHeight(), matrix, true);

                                              }

                                              // 360dp*640dp 화면에 맞춰 scale 조정
                                              float widthScale = resource.getWidth() / 360f;  // 아악 float 아아ㅏ아악
                                              float heightScale = resource.getHeight() / 640f;

                                              Matrix matrix = new Matrix();
                                              if (widthScale > heightScale) {
                                                  matrix.preScale(1 / widthScale, 1 / widthScale);
                                              } else {
                                                  matrix.preScale(1 / heightScale, 1 / heightScale);
                                              }
                                              resource = Bitmap.createBitmap(resource, 0, 0,
                                                      resource.getWidth(), resource.getHeight(), matrix, true);

                                              Log.d("Resource", "width: " + resource.getWidth() + ", height: " + resource.getHeight());

                                              // 360dp*640dp 화면에서 딱 맞지 않는 부분은 투명으로 채우기
                                              Bitmap result = Bitmap.createBitmap(360, 640, Bitmap.Config.ARGB_8888); // 기본이 투명
                                              Canvas canvas = new Canvas(result);
                                              Paint paint = new Paint();

                                              if (widthScale > heightScale) {
                                                  canvas.drawBitmap(resource, 0, (640 - resource.getHeight()) / 2f, paint);
                                              } else {
                                                  canvas.drawBitmap(resource, (360 - resource.getWidth()) / 2f, 0, paint);
                                              }

                                              // 비트맵을 drawable로 변환
                                              Drawable drawable = new BitmapDrawable(result);

                                              // add canvas view
                                              routeCanvasView = new RouteCanvasView(getActivity().getApplicationContext());
                                              frameLayout.addView(routeCanvasView);

                                              // 캔버스뷰에 background로 세팅
                                              routeCanvasView.setBackground(drawable); // background로 넣어야지만 drawPath 가능
                                          }
                                      });
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
                if (check == 0) {
                    Toast.makeText(getActivity(), "ImageFail", Toast.LENGTH_SHORT).show();
                } else {
                    String filename = "image" + fileId+".png";
                    File file = new File(getActivity().getFilesDir(), filename);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    StorageReference downLoadRef = storageRef.child("image" + fileId + ".png");

                    downLoadRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d("onSuccess", file.getPath());
                            Toast.makeText(getActivity(), "Download success", Toast.LENGTH_SHORT).show();

                            String buildingName = findPath.getBuildingName();
                            String address = findPath.getBuildingName();
                            String floorNum = findPath.getFloorNum();
                            String id = findPath.getId();
                            String nodeNum = findPath.getNodeNum();
                            String x = findPath.getX();
                            String y = findPath.getY();
                            String node = findPath.getNode();

                            // 내부 저장소의 bookmarklist.txt 파일 업데이트
                            updateBookmarkList(buildingName,address, floorNum, nodeNum, x, y, id, node);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("onFailure", file.getPath());
                        }
                    });
                }
            }
        });

        adjustNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, new Edit1CornerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    private void updateBookmarkList(String buildingName,String address, String floorNum,String nodeNum,String x, String y, String id, String node) {
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
            content.append("address: ").append(address).append("\n");
            content.append("floorNum: ").append(floorNum).append("\n");
            content.append("fileId: ").append(id).append("\n");
            content.append("nodeNum: ").append(nodeNum).append("\n");
            content.append("x: ").append(x).append("\n");
            content.append("y: ").append(y).append("\n");
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