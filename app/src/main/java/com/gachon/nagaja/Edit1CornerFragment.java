package com.gachon.nagaja;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
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

public class Edit1CornerFragment extends Fragment  {
    private Bitmap backgroundBitmap;
    private View rootView;

    FrameLayout frameLayout;
    CanvasView canvasView;
    FindPath findPath;
    Button addNodeButton;
    Button deleteNodeButton;
    Button moveToUpButton;
    Button moveToDownButton;
    Button moveToLeftButton;
    Button moveToRightButton;
    Button nextButton;


    public void setBackground(Bitmap bitmap) {
        backgroundBitmap = bitmap;
        if (rootView != null && backgroundBitmap != null) {
            rootView.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }
    }

    public void setFindPath(FindPath findPath){
        this.findPath = findPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit1_corner, container, false);
        if (backgroundBitmap != null) {
            rootView.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }
        frameLayout = rootView.findViewById(R.id.frameLayout);
        addNodeButton = rootView.findViewById(R.id.addNodeButton);
        deleteNodeButton = rootView.findViewById(R.id.deleteNodeButton);
        moveToUpButton = rootView.findViewById(R.id.moveToUpButton);
        moveToDownButton = rootView.findViewById(R.id.moveToDownButton);
        moveToLeftButton = rootView.findViewById(R.id.moveToLeftButton);
        moveToRightButton = rootView.findViewById(R.id.moveToRightButton);
        nextButton = rootView.findViewById(R.id.nextButton);

        // add canvas view
        canvasView = new CanvasView(getActivity().getApplicationContext(),findPath);
        frameLayout.addView(canvasView);

        // TODO: 파베에서 이미지 받아와서 canvasView에 background로 띄우는 코드
        // 원래는 이미지 들어가는데 테스트 용으로 지금만 color 넣음
//        canvasView.setBackgroundColor(Color.WHITE);
//        canvasView.setBackground(파베에서 받아온 사진 파일 drawable);

        // TODO: 파베에서 좌표 정보 받아오는 코드
        // 받아 온 좌표를 CanvasView에 있는 node_corner ArrayList에 넣기
//        canvasView.node_corner.add(new Point(82,64));

        // node 추가 버튼
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.node_corner.add(new Point(10,10));
                canvasView.curEdit = canvasView.node_corner.size() - 1; // 추가한 게 바로 초록색으로
                canvasView.invalidate();
            }
        });

        // node 삭제 버튼
        deleteNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.node_corner.remove(canvasView.curEdit);
                canvasView.curEdit = -1;
                canvasView.invalidate();
            }
        });

        // 상하좌우 버튼들
        moveToUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.moveNodeCoordinate("up");
            }
        });
        moveToDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.moveNodeCoordinate("down");
            }
        });
        moveToLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.moveNodeCoordinate("left");
            }
        });
        moveToRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.moveNodeCoordinate("right");
            }
        });

        // 다음 단계로 넘어가는 버튼
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // curEdit 설정
                canvasView.curEdit = -2;

                // TODO: 넘어가는 코드
                // 변수 안꼬이게 activity 넘기고 나서 쓰는 finish()같은 거 넣어 주기. 이전 화면으로 못돌아오도록
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Edit2HallwayFragment edit2HallwayFragment = new Edit2HallwayFragment();
                edit2HallwayFragment.setBackground(backgroundBitmap);
                edit2HallwayFragment.setFindPath(findPath);
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, edit2HallwayFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        return rootView;
    }

}