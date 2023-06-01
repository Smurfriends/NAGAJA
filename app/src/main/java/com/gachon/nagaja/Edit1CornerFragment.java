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
    }

    public void setFindPath(FindPath findPath){
        this.findPath = findPath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit1_corner, container, false);

        frameLayout = rootView.findViewById(R.id.frameLayout);
        addNodeButton = rootView.findViewById(R.id.addNodeButton);
        deleteNodeButton = rootView.findViewById(R.id.deleteNodeButton);
        moveToUpButton = rootView.findViewById(R.id.moveToUpButton);
        moveToDownButton = rootView.findViewById(R.id.moveToDownButton);
        moveToLeftButton = rootView.findViewById(R.id.moveToLeftButton);
        moveToRightButton = rootView.findViewById(R.id.moveToRightButton);
        nextButton = rootView.findViewById(R.id.nextButton);

        // frameLayout에 이미지 세팅
        if (backgroundBitmap != null) {
            frameLayout.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }

        // add canvas view
        canvasView = new CanvasView(getActivity().getApplicationContext(),findPath);
        frameLayout.addView(canvasView);

        // node 추가 버튼
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.setMatrixAfterAddCornerNode();    // matrix에 반영
                canvasView.node_corner.add(new Point(20,20));
                canvasView.curEdit = canvasView.node_corner.size() - 1; // 추가한 게 바로 초록색으로

                canvasView.invalidate();
            }
        });

        // node 삭제 버튼
        deleteNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (canvasView.curEdit != -1) {
                    canvasView.setMatrixAfterDeleteCornerNode();    // matrix에 반영
                    canvasView.node_corner.remove(canvasView.curEdit);
                    canvasView.curEdit = -1;

                    canvasView.invalidate();
                }
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

                // corner 노드가 바꼈으니 matrix 가중치 재계산
                canvasView.recalculateMatrixWeights();

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