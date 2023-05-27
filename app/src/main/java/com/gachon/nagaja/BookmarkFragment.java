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
    private String fileId;
    private String nodeNum;
    private String x;
    private String y;
    private String node;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bookmark, container, false);

        listView = rootView.findViewById(R.id.bookmarkList);

        ListItem item = new ListItem("bname","buildingName", "floorNum", "fireId","nodeNum","x","y","node");
        adapter = new ListItemAdapter();

        readBookmarkList();
        listView.setAdapter(adapter);

        // Set item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked ListItem
                ListItem clickedItem = (ListItem) adapter.getItem(position);


                // Create a new instance of BookmarkInnerFragment
                BookmarkInnerFragment innerFragment = BookmarkInnerFragment.newInstance(clickedItem);
                innerFragment.setPosition(position);

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
                    // Read the item details
                    String buildingName = null;
                    String address = null;
                    String floorNum = null;
                    String fileId = null;
                    String nodeNum = null;
                    String x = null;
                    String y = null;
                    String node = null;

                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("buildingName: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                buildingName = parts[1];
                            }
                        } else if (line.startsWith("address: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                address = parts[1];
                            }
                        } else if (line.startsWith("floorNum: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                floorNum = parts[1];
                            }
                        } else if (line.startsWith("fileId: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                fileId = parts[1];
                            }
                        } else if (line.startsWith("nodeNum: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                nodeNum = parts[1];
                            }
                        } else if (line.startsWith("x: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                x = parts[1];
                            }
                        } else if (line.startsWith("y: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                y = parts[1];
                            }
                        } else if (line.startsWith("node: ")) {
                            String[] parts = line.split(": ");
                            if (parts.length > 1) {
                                node = parts[1];
                            }
                        } else if (line.equals("?")) {
                            // End of item details
                            break;
                        }
                    }

                    Log.d("BookmarkFragment", fileId);
                    // Create a new ListItem and add it to the adapter
                    ListItem item = new ListItem(buildingName, address, floorNum, fileId, nodeNum, x, y, node);
                    adapter.addItem(item);
                    // TODO: Add the item to the adapter or perform any desired operations
                }
            }

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("BookmarkFragment", "Error reading bookmarklist.txt file");
        }
    }


}
