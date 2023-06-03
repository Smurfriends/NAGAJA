package com.gachon.nagaja;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class Edit3ExitFragment extends Fragment  {
    private Bitmap backgroundBitmap;
    private View rootView;
    FrameLayout frameLayout;
    CanvasView canvasView;
    FindPath findPath;
    Button deselectButton;
    Button addExitButton;
    Button confirmButton;
    Button doneButton;

    public void setBackground(Bitmap bitmap) {
        backgroundBitmap = bitmap;
    }
    public void setFindPath(FindPath findPath){
        this.findPath = findPath;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit3_exit, container, false);

        frameLayout = rootView.findViewById(R.id.frameLayout);
        deselectButton = rootView.findViewById(R.id.deselectButton);
        addExitButton = rootView.findViewById(R.id.addExitButton);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        doneButton = rootView.findViewById(R.id.doneButton);

        if (backgroundBitmap != null) {
            frameLayout.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }

        // add canvas view
        canvasView = new CanvasView(getActivity().getApplicationContext(), findPath);
        frameLayout.addView(canvasView);

        // node 선택 해제 버튼
        deselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;
                if (canvasView.curDrag != -1) { // add 중이었다면
                       canvasView.node_exit.remove(canvasView.curDrag);
                    canvasView.curDrag = -1;
                }
                canvasView.invalidate();
            }
        });

        // Exit node 추가 버튼
        addExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.addExitNode();
            }
        });


        // Exit node의 좌표 조정을 끝내서 확정하는 버튼 (이후 수정 불가)
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("exitNode x", String.valueOf(canvasView.node_exit.get(canvasView.curDrag).x));
                Log.d("exitNode y", String.valueOf(canvasView.node_exit.get(canvasView.curDrag).y));
                Log.d("exitNode coefficient", String.valueOf(canvasView.node_exit.get(canvasView.curDrag).coefficient));
                Log.d("exitNode constant", String.valueOf(canvasView.node_exit.get(canvasView.curDrag).constant));

                // 다 초기화
                canvasView.curDrag = -1;
                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;
                canvasView.invalidate();
            }
        });

        // 수정 완료 버튼
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // exit node를 포함하게 matrix 재생성 (가중치 계산 필요)
                canvasView.addEdgeOfExitNodeToMatrix();

                // 노드 좌표 정보를 node_corner 이용해서 한번에 정리하기 위해 합치기
                canvasView.combineExitNodeToCornerNodeList();

                // 파이어베이스에 수정된 node 정보들 업데이트
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();

                databaseReference.child("map").child(findPath.getBuildingName())
                        .child("node").setValue(findPath.getNodeToString());
                databaseReference.child("map").child(findPath.getBuildingName())
                        .child("nodeNum").setValue(findPath.getNodeNum());
                databaseReference.child("map").child(findPath.getBuildingName())
                        .child("x").setValue(findPath.getXToString());
                databaseReference.child("map").child(findPath.getBuildingName())
                        .child("y").setValue(findPath.getYToString());


                // curEdit 초기화
                canvasView.curEdit = 0;

                // TODO: 홈 화면이든 북마크 화면이든 넘어가는 코드
                // 변수 안꼬이게 activity 넘기고 나서 쓰는 finish()같은 거 넣어 주기. 이전 화면으로 못돌아오도록

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.menu_frame_layout, new ImageFragment(findPath.getBuildingName()))
                                .addToBackStack(null)
                                .commit();
                    }
                    }, 1000);
            }
        });

        return rootView;
    }

}