package com.gachon.nagaja;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FindPathUnitTest {

    private double[][] graph;
    int startNode;
    int endNode;

    @Before
    public void setUp() {
        graph = new double[][]{
                {0, 2, 4, 0, 0},
                {2, 0, 1, 3, 0},
                {4, 1, 0, 2, 3},
                {0, 3, 2, 0, 6},
                {0, 0, 3, 6, 0}
        };

        startNode = 0;
        endNode = 4;


    }

    @Test
    public void addTest() {
        StringBuilder expected = new StringBuilder();
        expected.append("0 -> 1 -> 2 -> 4");
        StringBuilder result = FindPath.dijkstra(graph, startNode, endNode);
        assertEquals(expected.toString(), result.toString());
    }
}

