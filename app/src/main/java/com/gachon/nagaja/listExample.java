package com.gachon.nagaja;

//listView 사용 포맷 예시라 사용하시고 지워주시면 될것 같습니다

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class listExample extends Fragment {
    ListView listView;
    ListItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        adapter = new ListItemAdapter();

        adapter.addItem(new ListItem("건물이름", "건물주소"));
        listView.setAdapter(adapter);

        return rootView;
    }
}
