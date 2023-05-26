package com.gachon.nagaja;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BookmarkFragment extends Fragment {
    ListView listView;
    public ListItemAdapter adapter;

    // Global variables to store the selected bookmark information
    private String selectedBuildingName;
    private String selectedFloorNum;
    private String fireId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        adapter = new ListItemAdapter();

        ListItem item1 = new ListItem("buildingName", "floorNum", "imgUrl");
        adapter.addItem(item1);
        readBookmarkList();
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked ListItem
                ListItem clickedItem = (ListItem) adapter.getItem(position);

                // Update the selected bookmark information
                selectedBuildingName = clickedItem.getBuildingName();
                selectedFloorNum = clickedItem.getFloorNum();
                fireId = clickedItem.getId();

                // Create a new instance of BookmarkInnerFragment
                BookmarkInnerFragment innerFragment = BookmarkInnerFragment.newInstance(selectedBuildingName, selectedFloorNum, fireId);

                // Navigate to BookmarkInnerFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.menu_frame_layout, innerFragment)
                        .addToBackStack(null)
                        .commit();

                Log.d("Bookmark","Bookmark" + position);
            }
        });

        return rootView;
    }

    private void readBookmarkList() {
        String fileName = "bookmarklist.txt";

        try {
            FileInputStream fis = getActivity().openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                // Check if the line indicates the start of a new item
                if (line.equals("?")) {
                    // Read the item details 수정해야함 그냥 위에서 3개 읽어버림 멍청함
                    String buildingName = reader.readLine().split(": ")[1];
                    String floorNum = reader.readLine().split(": ")[1];
                    String fireId = reader.readLine().split(": ")[1];
                    Log.d("BookmarkFragment", fireId);
                    // Create a new ListItem and add it to the adapter
                    ListItem item = new ListItem(buildingName, floorNum, fireId);
                    adapter.addItem(item);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("BookmarkFragment", "bookmarklist.txt 파일 읽기 오류");
        }
    }

}
