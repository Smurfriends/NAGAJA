package com.gachon.nagaja;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class FindPathByTxt extends FindPath {
    private String txtFilePath;
    String floorNum;
    String nodeNum;
    String x;
    String y;
    String id;
    String node;

    public FindPathByTxt(String bname, String buildingName, String floorNum, String nodeNum, String x, String y, String id, String node) {
        super(bname);
        this.node = node;
        this.nodeNum = nodeNum;
        this.id = id;
        this.x = x;
        this.y = y;

        this.floorNum = floorNum;
    }



}
