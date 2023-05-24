package com.gachon.nagaja;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;

public class FindPath {
    //Firebase read
    public DatabaseReference database;
    private int x;
    private int y;
    private static final int INFINITY = Integer.MAX_VALUE;

    public FindPath(int x, int y,String name){//like mains
        database=FirebaseDatabase.getInstance().getReference();
        database.child("map").child(name).addValueEventListener(postListener);
        this.x = x;
        this.y = y;
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();

            // Extract individual values and store them in separate variables
            String node = getStringValue(hashMap, "node");
            String idEndNode = getStringValue(hashMap, "idEndNode");
            String nodeNum = getStringValue(hashMap, "nodeNum");
            String idVergeNode = getStringValue(hashMap, "idVergeNode");
            String mapURL = getStringValue(hashMap, "mapURL");
            String x = getStringValue(hashMap, "x");
            String y = getStringValue(hashMap, "y");

            // Output or perform desired operations with the extracted values
            Log.d("Firebase", "node: " + node);
            Log.d("Firebase", "idEndNode: " + idEndNode);
            Log.d("Firebase", "nodeNum: " + nodeNum);
            Log.d("Firebase", "idVergeNode: " + idVergeNode);
            Log.d("Firebase", "mapURL: " + mapURL);
            Log.d("Firebase", "x: " + x);
            Log.d("Firebase", "y: " + y);

            String values = node;
            String[] splitValues = values.split(",");

            int matrixSize = Integer.parseInt(nodeNum); // Size of the matrix
            int[][] matrix = new int[matrixSize][matrixSize];

            // Convert the split values to integers and populate the matrix
            int index = 0;
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    matrix[i][j] = Integer.parseInt(splitValues[index]);
                    index++;
                }
            }

            // Log the resulting matrix
            for (int i = 0; i < matrixSize; i++) {
                StringBuilder rowBuilder = new StringBuilder();
                for (int j = 0; j < matrixSize; j++) {
                    rowBuilder.append(matrix[i][j]).append(" ");
                }
                Log.d("Matrix", rowBuilder.toString());
            }
            int startNode = 0;
            int endNode = 4;

            dijkstra(matrix, startNode, endNode);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("loadPost:onCancelled", databaseError.toException());
        }

        // Helper method to safely retrieve a String value from HashMap
        private String getStringValue(HashMap<String, Object> hashMap, String key) {
            Object value = hashMap.get(key);
            if (value != null) {
                return String.valueOf(value);
            } else {
                return ""; // Return empty string if the value is null
            }
        }
    };


    public static void dijkstra(int[][] graph, int startNode, int endNode) {
        int numNodes = graph.length;
        boolean[] visited = new boolean[numNodes];
        int[] distance = new int[numNodes];
        int[] previous = new int[numNodes];
        Arrays.fill(distance, INFINITY);
        Arrays.fill(previous, -1);
        distance[startNode] = 0;

        for (int i = 0; i < numNodes - 1; i++) {
            int minDistanceNode = getMinDistanceNode(distance, visited);
            visited[minDistanceNode] = true;

            for (int j = 0; j < numNodes; j++) {
                if (!visited[j] && graph[minDistanceNode][j] != 0 && distance[minDistanceNode] != INFINITY &&
                        distance[minDistanceNode] + graph[minDistanceNode][j] < distance[j]) {
                    distance[j] = distance[minDistanceNode] + graph[minDistanceNode][j];
                    previous[j] = minDistanceNode;
                }
            }
        }

        logShortestPath(startNode, endNode, previous);
    }
    private static int getMinDistanceNode(int[] distance, boolean[] visited) {
        int minDistance = INFINITY;
        int minDistanceNode = -1;
        int numNodes = distance.length;

        for (int i = 0; i < numNodes; i++) {
            if (!visited[i] && distance[i] <= minDistance) {
                minDistance = distance[i];
                minDistanceNode = i;
            }
        }

        return minDistanceNode;
    }
    private static void logShortestPath(int startNode, int endNode, int[] previous) {
        String tag = "Dijkstra";
        Log.d(tag, "Shortest path from Node " + startNode + " to Node " + endNode + ":");

        if (previous[endNode] == -1) {
            Log.d(tag, "No path found.");
        } else {
            StringBuilder pathBuilder = new StringBuilder();
            pathBuilder.append(endNode);

            int node = endNode;
            while (node != startNode) {
                node = previous[node];
                pathBuilder.insert(0, node + " -> ");
            }

            Log.d(tag, pathBuilder.toString());
        }
    }
}
