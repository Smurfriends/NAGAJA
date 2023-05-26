package com.gachon.nagaja;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

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
            String filename = "image" + fileId + ".png";
            File fileDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/map_img");

            File file = new File(fileDir, filename);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            Toast.makeText(getActivity(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
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

        return rootView;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
