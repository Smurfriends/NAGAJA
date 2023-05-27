package com.gachon.nagaja;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class bookmarkFragment extends Fragment {

    ListView listView;
    ListItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        adapter = new ListItemAdapter();

        adapter.addItem(new ListItem("건물이름", "건물주소"));
        adapter.addItem(new ListItem("건물이름", "건물주소"));
        adapter.addItem(new ListItem("건물이름", "건물주소"));
        listView.setAdapter(adapter);

        return rootView;
    }
}