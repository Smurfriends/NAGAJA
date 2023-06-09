package com.gachon.nagaja;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class Edit2HallwayFragment extends Fragment  {
    private Bitmap backgroundBitmap;
    private View rootView;
    FrameLayout frameLayout;
    CanvasView canvasView;
    FindPath findPath;
    Button deselectButton;
    Button connectButton;
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
        rootView = inflater.inflate(R.layout.fragment_edit2_hallway, container, false);

        frameLayout = rootView.findViewById(R.id.frameLayout);
        deselectButton = rootView.findViewById(R.id.deselectButton);
        connectButton = rootView.findViewById(R.id.connectButton);
        nextButton = rootView.findViewById(R.id.nextButton);

        // frameLayout에 이미지 세팅
        if (backgroundBitmap != null) {
            frameLayout.setBackground(new BitmapDrawable(getResources(), backgroundBitmap));
        }

        // add canvas view
        canvasView = new CanvasView(getActivity().getApplicationContext(),findPath);
        frameLayout.addView(canvasView);

        // node 선택 해제 버튼
        deselectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;
                canvasView.invalidate();
            }
        });


        // node 연결 버튼
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.editConnection();
            }
        });

        // 다음 단계로 넘어가는 버튼
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // curEditTwo 초기화
                canvasView.curEditTwo[0] = -1;
                canvasView.curEditTwo[1] = -1;

                // curEdit 설정
                canvasView.curEdit = -3;

                // matirx 확인 출력
//                for (int row = 0; row < canvasView.node_corner.size(); row++) {
//                    for (int col = 0; col < canvasView.node_corner.size(); col++) {
//                        System.out.print(canvasView.matrix[row][col] + ", ");
//                    }
//                    System.out.println("");
//                }

                // TODO: 넘어가는 코드
                // 변수 안꼬이게 activity 넘기고 나서 쓰는 finish()같은 거 넣어 주기. 이전 화면으로 못돌아오도록
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Edit3ExitFragment edit3ExitFragment = new Edit3ExitFragment();
                edit3ExitFragment.setBackground(backgroundBitmap);
                edit3ExitFragment.setFindPath(findPath);
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, edit3ExitFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        return rootView;
    }

}