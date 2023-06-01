package com.gachon.nagaja;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.images.ImageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BookmarkInnerFragment extends Fragment {

    int position;
    ListItem item;


    TextView bookmark;
    FrameLayout frameLayout;
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
    RouteCanvasView routeCanvasView;

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
        frameLayout = rootView.findViewById(R.id.frameLayout);

        Button deleteBtn = rootView.findViewById(R.id.deleteButton);

        bname = item.getBname();
        buildingName = item.getBuildingName();
        floorNum = item.getFloorNum();
        fileId = item.getId();
        nodenum = item.getNodeNum();
        x = item.getX();
        y = item.getY();
        node = item.getNode();

        bookmark.setText(buildingName);

        Log.d("InnerFragment", "nodeNum: " + nodenum);
        Log.d("InnerFragment", "fileId: " + fileId);
        Log.d("InnerFragment", "x: " + x);
        Log.d("InnerFragment", "y: " + y);
        Log.d("InnerFragment", "node: " + node);

        //TODO 아래 코드들 정리해야함.
        findPathByTxt = new FindPathByTxt(bname,buildingName,floorNum,nodenum,x,y,fileId,node);
        //node point 생성 집어넣기
        findPathByTxt.setNodeArrayList(x,y);// setNode first.
        ArrayList<Point> nodeArray = findPathByTxt.getNodeArrayList();
        //matrix 생성 집어넣기
        findPathByTxt.setMatrix(node, Integer.parseInt(nodenum));// setMatrix second
        ArrayList<double[][]> matrix = findPathByTxt.getMatrix();

        routeCanvasView = new RouteCanvasView(getActivity().getApplicationContext(),findPathByTxt);

        //하나하나 집어 넣어야함
        routeCanvasView.setNode(nodeArray);//1
        routeCanvasView.setMatrix(matrix); //2
        frameLayout.addView(routeCanvasView);

        try {
            String filename = "image" + fileId;
            File pngFile = new File(getActivity().getFilesDir(), filename + ".png");

            Bitmap bitmap = null;

            if (pngFile.exists()) {
                bitmap = BitmapFactory.decodeFile(pngFile.getAbsolutePath());
            }

            if (bitmap != null) {

                Drawable drawable = new BitmapDrawable(bitmap);

                routeCanvasView.setBackground(drawable);
                Toast.makeText(getActivity(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }



        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveBookmarkList(position);
                Toast.makeText(getActivity(),"삭제 되었습니다",Toast.LENGTH_SHORT).show();
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

        for (String line : lines) {
            if (line.equals("?")) {
                if (count != position) {
                    content.append(line).append("\n");
                }
                count++;
            } else {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

}
