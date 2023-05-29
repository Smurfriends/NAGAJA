package com.gachon.nagaja;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class Guide2ShowRouteFragment extends Fragment  {
    FrameLayout frameLayout;
    RouteCanvasView routeCanvasView;
    FindPath findPath;
    Button backToMainButton;
    ActivityMenu activityMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guide2_show_route, container, false);
        frameLayout = rootView.findViewById(R.id.frameLayout);

        backToMainButton = rootView.findViewById(R.id.backToMainButton);

        // add canvas view
        routeCanvasView = new RouteCanvasView(getActivity().getApplicationContext());
        frameLayout.addView(routeCanvasView);

        // TODO: 파베에서 이미지 받아와서 canvasView에 background로 띄우는 코드
        // 원래는 이미지 들어가는데 테스트 용으로 지금만 color 넣음
        routeCanvasView.setBackgroundColor(Color.WHITE);
//        canvasView.setBackground(파베에서 받아온 사진 파일 drawable);

        // 피난안내도 보여주도록 넘어가는 버튼
        backToMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                // TODO: routeCanvasView 변수 초기화
                routeCanvasView.showPath = false;

                // TODO: 메인화면으로 넘어가기
//                activityMenu.changeFragment(2);
            }
        });

        return rootView;
    }

}