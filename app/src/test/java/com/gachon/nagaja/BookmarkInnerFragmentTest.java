package com.gachon.nagaja;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class BookmarkInnerFragmentTest {

    String bookmarkListData ;
    int position;

    @Before
    public void setUp(){
        bookmarkListData  = "?\n" +
                "buildingName: Building 0\n" +
                "address: Address 0\n" +
                "floorNum: Floor 0\n" +
                "fileId: ID 0\n" +
                "nodeNum: Node 0\n" +
                "x: X 0\n" +
                "y: Y 0\n" +
                "node: Node 0\n" +
                "?\n" +
                "buildingName: Building 1\n" +
                "address: Address 1\n" +
                "floorNum: Floor 1\n" +
                "fileId: ID 1\n" +
                "nodeNum: Node 1\n" +
                "x: X 1\n" +
                "y: Y 1\n" +
                "node: Node 1\n";
        position = 0;
    }

    @Test
    public void addTest(){
        String expected = "?\n" +
                "buildingName: Building 1\n" +
                "address: Address 1\n" +
                "floorNum: Floor 1\n" +
                "fileId: ID 1\n" +
                "nodeNum: Node 1\n" +
                "x: X 1\n" +
                "y: Y 1\n" +
                "node: Node 1\n";

        String result = BookmarkInnerFragment.DeleteFromBookmarkList(bookmarkListData,position);
        assertEquals(expected,result);
    }
}
