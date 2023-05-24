package com.gachon.nagaja;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class BookmarkFragment extends Fragment {
    ListView listView;
    ListItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        adapter = new ListItemAdapter();
        adapter.addItem(new ListItem("건물이름", "건물주소"));
        listView.setAdapter(adapter);
//        다익스트라 길찾기 알고리즘 사용법
//        FindPath findPath = new FindPath(0,0,"경기도 어쩌구 수정구 저쩌구");
        
        return rootView;


    }
}
