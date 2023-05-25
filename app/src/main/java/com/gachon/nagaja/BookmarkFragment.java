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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BookmarkFragment extends Fragment {
    ListView listView;
    public ListItemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        adapter = new ListItemAdapter();

        readBookmarkList();
        listView.setAdapter(adapter);

//        // Set item click listener
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Get the clicked ListItem
//                ListItem clickedItem = adapter.getItem(position);
//
//                // Extract the necessary information
//                String buildingName = clickedItem.getBuildingName();
//                String floorNum = clickedItem.getFloorNum();
//                String imgUrl = clickedItem.getImgUrl();
//
//                // Create a new instance of BookmarkInnerFragment
//                BookmarkInnerFragment innerFragment = BookmarkInnerFragment.newInstance(buildingName, floorNum, imgUrl);
//
//                // Navigate to BookmarkInnerFragment
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, innerFragment)
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

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
                    // Read the item details
                    String buildingName = reader.readLine().split(": ")[1];
                    String floorNum = reader.readLine().split(": ")[1];
                    String imgUrl = reader.readLine().split(": ")[1];

                    // Create a new ListItem and add it to the adapter
                    ListItem item = new ListItem(buildingName, floorNum, imgUrl);
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
