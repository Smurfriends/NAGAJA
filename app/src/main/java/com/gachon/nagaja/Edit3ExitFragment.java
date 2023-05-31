package com.gachon.nagaja;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

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
        if (rootView != null && backgroundBitmap != null) {
            rootView.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }
    }
    public void setFindPath(FindPath findPath){
        this.findPath = findPath;
    }
    public void setCanvasView(CanvasView canvasView){
        this.canvasView = canvasView;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit3_exit, container, false);
        if (backgroundBitmap != null) {
            rootView.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }
        frameLayout = rootView.findViewById(R.id.frameLayout);
        deselectButton = rootView.findViewById(R.id.deselectButton);
        addExitButton = rootView.findViewById(R.id.addExitButton);
        confirmButton = rootView.findViewById(R.id.confirmButton);
        doneButton = rootView.findViewById(R.id.doneButton);

        // add canvas view
//        canvasView = new CanvasView(getActivity().getApplicationContext(), findPath);
        frameLayout.addView(canvasView);

        // TODO: 파베에서 이미지 받아와서 canvasView에 background로 띄우는 코드
        // 원래는 이미지 들어가는데 테스트 용으로 지금만 color 넣음
//        canvasView.setBackgroundColor(Color.WHITE);
//        canvasView.setBackground(파베에서 받아온 사진 파일 drawable);

        // node 선택 해제 버튼
        deselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;
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
                canvasView.addEdgeOfExitNodeToMatrix(); // 이 함수에 파베에 edge matrix 업로드하는 거 포함되어있음

                // 파이어베이스에 노드 좌표 정보, nodeNum(EndNode를 가르는 기준 index) 업로드
                int indexExitNodeStart = canvasView.combineExitNodeToCornerNodeList();  // 노드 좌표 정보를 node_corner 이용해서 한번에 정리하기 위해 합치기
                // nodeNum에 넣기. indexExitNodeStart까지는 isEndNode=false, 이후는 isEndNode=true 가 되는 셈
                // TODO: 파베에 노드 좌표 정보, nodeNum 업로드

                // canvasView에 있는 변수들 초기화
                canvasView.curEdit = 0;
                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;
                canvasView.curDrag = -1;
                canvasView.node_corner.clear();
                canvasView.node_exit.clear();
                canvasView.matrix.clear();

                // TODO: 홈 화면이든 북마크 화면이든 넘어가는 코드
                // 변수 안꼬이게 activity 넘기고 나서 쓰는 finish()같은 거 넣어 주기. 이전 화면으로 못돌아오도록
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, new ImageFragment(findPath.getBuildingName()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

}