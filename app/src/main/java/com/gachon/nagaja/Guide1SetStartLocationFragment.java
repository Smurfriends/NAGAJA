package com.gachon.nagaja;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class Guide1SetStartLocationFragment extends Fragment  {
    FrameLayout frameLayout;
    RouteCanvasView routeCanvasView;
    FindPath findPath;
    Button showEvacuationRouteButton;
    ActivityMenu activityMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guide1_set_start_location, container, false);
        frameLayout = rootView.findViewById(R.id.frameLayout);

        showEvacuationRouteButton = rootView.findViewById(R.id.showEvacuationRouteButton);

        // ActivityMenu
//        activityMenu = new ActivityMenu();

        // add canvas view
        routeCanvasView = new RouteCanvasView(getActivity().getApplicationContext(),"ai");
        frameLayout.addView(routeCanvasView);

        // TODO: 파베에서 이미지 받아와서 canvasView에 background로 띄우는 코드
        // 원래는 이미지 들어가는데 테스트 용으로 지금만 color 넣음
        routeCanvasView.setBackgroundColor(Color.WHITE);
//        canvasView.setBackground(파베에서 받아온 사진 파일 drawable);
        
        // 피난안내도 보여주도록 넘어가는 버튼
        showEvacuationRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                routeCanvasView.setStartNode(); // 시작 노드 설정
                routeCanvasView.addEdgeOfStartNodeToMatrix();   // 시작 노드의 edge 2개를 matrix에 추가
                routeCanvasView.findShortestPathToAllExits(4);    // exit 노드 개수만큼 다익스트라
                routeCanvasView.showPath = true;    // 다음 화면에서 drawPath 코드 실행되도록

                // TODO: path 그려주는 화면으로 넘어가는 코드
//                ActivityMenu.changeFragment(1);
//                FragmentTransaction transaction = ActivityMenu.fragmentManager.beginTransaction();
//                transaction.replace(R.id.menu_frame_layout, new Guide1SetStartLocationFragment()).commit();
            }
        });

        return rootView;
    }

}