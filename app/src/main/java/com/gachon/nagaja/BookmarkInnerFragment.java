package com.gachon.nagaja;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BookmarkInnerFragment extends Fragment {

    int position;
    ListItem item;


    TextView bookmark;
    ImageView imageView;
    TextView info;

    String bname;
    String buildingName;
    String floorNum;
    String fileId;
    String nodenum;
    String x;
    String y;
    String node;


    FindPathByTxt findPathByTxt;
//    public static BookmarkInnerFragment newInstance(String selectedBuildingName, String selectedFloorNum, String fireId) {
//        BookmarkInnerFragment fragment = new BookmarkInnerFragment();
//        fragment.buildingName = selectedBuildingName;
//        fragment.fireId = fireId;
//        fragment.floorNum = selectedFloorNum;
//
//
//
//        return fragment;
//    }
    public static BookmarkInnerFragment newInstance(ListItem thisItem) {
        BookmarkInnerFragment fragment = new BookmarkInnerFragment();
        fragment.item = thisItem;

        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bookmark_inner, container, false);

        bookmark = rootView.findViewById(R.id.bookmark);
        info = rootView.findViewById(R.id.info);
        imageView = rootView.findViewById(R.id.img);

        Button deleteBtn = rootView.findViewById(R.id.deleteButton);

        bname = item.getBname();
        buildingName = item.getBuildingName();
        floorNum = item.getFloorNum();
        fileId = item.getId();
        nodenum = item.getNodeNum();
        x = item.getX();
        y = item.getY();
        node = item.getNode();

        findPathByTxt = new FindPathByTxt(bname,buildingName,floorNum,fileId,nodenum,x,y,node);

        Log.d("InnerFragment", "nodeNum: " + nodenum);
        Log.d("InnerFragment", "fileId: " + fileId);
        Log.d("InnerFragment", "x: " + x);
        Log.d("InnerFragment", "y: " + y);
        Log.d("InnerFragment", "node: " + node);



        try {
            String filename = "image" + fileId;
            File pngFile = new File(getActivity().getFilesDir(), filename + ".png");

            Bitmap bitmap = null;

            if (pngFile.exists()) {
                bitmap = BitmapFactory.decodeFile(pngFile.getAbsolutePath());
            }

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                Toast.makeText(getActivity(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }

        bookmark.setText(buildingName);
        info.setText(floorNum);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                findPathByTxt.setMatrix();
                view.onTouchEvent(motionEvent);

                float[] point = new float[] {motionEvent.getX(), motionEvent.getY()};
                Log.d("InnerTouch", "변환 전: ("+point[0] + " , " + point[1] + ")");

                // match with image
                float density = getResources().getDisplayMetrics().density;
                point[0] /= density;
                point[1] /= density;

                if (motionEvent.getAction() ==  MotionEvent.ACTION_DOWN)
                {
                    Log.d("InnerPoint", "변환 후: ("+point[0] + " , " + point[1] + ")");
                }


                return false;
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveBookmarkList(position);
            }
        });

        return rootView;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    private void RemoveBookmarkList(int position) {
        String fileName = "bookmarklist.txt";
        FileInputStream fis;
        try {
            fis = getActivity().openFileInput(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String bookmarkListData = stringBuilder.toString();

        String content = DeleteFromBookmarkList(bookmarkListData, position);

        // Update the bookmark list with the modified content
        FileOutputStream fos;
        try {
            fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String DeleteFromBookmarkList(String bookmarkListData, int position) {
        String[] lines = bookmarkListData.split("\n");
        StringBuilder content = new StringBuilder();

        int count = 0;
        boolean skip = false;

        for (String line : lines) {
            if (line.equals("?")) {
                if (count == position) {
                    skip = true;
                } else {
                    skip = false;
                    content.append(line).append("\n");
                }
                count++;
            } else if (!skip) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

}
