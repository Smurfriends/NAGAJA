package com.gachon.nagaja;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
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

    TextView bookmark;
    ImageView imageView;
    TextView info;

    String buildingName;
    String fireId;
    String floorNum;

    public static BookmarkInnerFragment newInstance(String selectedBuildingName, String selectedFloorNum, String fireId) {
        BookmarkInnerFragment fragment = new BookmarkInnerFragment();
        fragment.buildingName = selectedBuildingName;
        fragment.fireId = fireId;
        fragment.floorNum = selectedFloorNum;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.bookmark_inner, container, false);

        bookmark = rootView.findViewById(R.id.bookmark);
        info = rootView.findViewById(R.id.info);
        imageView = rootView.findViewById(R.id.img);


        try {
            String filename = "image" + fireId + ".png";
            File fileDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/map_img");
            if (fileDir != null) {
                File file = new File(fileDir, filename);
                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        Toast.makeText(getActivity(), "파일 로드 성공", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("BookmarkInnerFragment", "Failed to decode bitmap from file");
                        Toast.makeText(getActivity(), "파일 로드 실패: 비트맵 디코딩 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("BookmarkInnerFragment", "File does not exist: " + file.getAbsolutePath());
                    Toast.makeText(getActivity(), "파일 로드 실패: 파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("BookmarkInnerFragment", "File directory is null");
                Toast.makeText(getActivity(), "파일 로드 실패: 파일 디렉토리를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
        }
        bookmark.setText(buildingName);
        info.setText(floorNum);

        return rootView;
    }
}
