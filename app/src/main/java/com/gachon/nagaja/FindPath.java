package com.gachon.nagaja;

import android.graphics.Point;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FindPath {
    //Firebase read
    public DatabaseReference database;
    private static final double INFINITY = Double.POSITIVE_INFINITY;
    private String node;
    private String nodeNum;
    private String id;
    private String x;
    private String y;

    private String address;
    private String floorNum;

    private String buildingName;

    private double[][] matrix;

    private ArrayList<Point> nodeArrayList;
    private ArrayList<Integer> pathIndex;

    public FindPath(String buildingName) {//like mains
        this.buildingName = buildingName;
        setData();
    }

    private void setData() {
        database = FirebaseDatabase.getInstance().getReference();
        database.child("map").child(buildingName).addValueEventListener(postListener);
        database.child("building").child(buildingName).addValueEventListener(postListener1);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();

            // Extract individual values and store them in separate variables
            node = getStringValue(hashMap, "node");
            nodeNum = getStringValue(hashMap, "nodeNum");
            id = getStringValue(hashMap, "id");
            x = getStringValue(hashMap, "x");
            y = getStringValue(hashMap, "y");

            setMatrix();
            setNodeArrayList();

            // Output or perform desired operations with the extracted values
            Log.d("Firebase", "node: " + node);
            Log.d("Firebase", "nodeNum: " + nodeNum);
            Log.d("Firebase", "mapURL: " + id);
            Log.d("Firebase", "x: " + x);
            Log.d("Firebase", "y: " + y);

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("loadPost:onCancelled", databaseError.toException());
        }

        // Helper method to safely retrieve a String value from HashMap

    };

    private String getStringValue(HashMap<String, Object> hashMap, String key) {
        Object value = hashMap.get(key);
        if (value != null) {
            return String.valueOf(value);
        } else {
            return ""; // Return empty string if the value is null
        }
    }

    public void setMatrix() {
        String values = node;
        String[] splitValues = values.split(",");

        int matrixSize = Integer.parseInt(nodeNum); // Size of the matrix
        if (matrixSize == -1) {
            // findPath의 nodeNum 값이 -1인 경우 아무런 반응이 없도록 처리
            Log.e("matrix","No node info");
        }else {
            matrix = new double[matrixSize][matrixSize];

            // Convert the split values to doubles and populate the matrix
            int index = 0;
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    if (splitValues[index].equals("100000")) {
                        matrix[i][j] = Double.POSITIVE_INFINITY; // or any other appropriate value
                    } else {
                        matrix[i][j] = Double.parseDouble(splitValues[index])/2;//크기를 반으로 줄이기 때문에 2를 나눠야함
                    }
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

//            StringBuilder road = dijkstra(matrix, startNode, endNode);
//            Log.d("dijkstra",road.toString());
        }

    }

    public void setNodeArrayList() {
        String[] splitValuesX = x.split(", ");
        String[] splitValuesY = y.split(", ");
        nodeArrayList = new ArrayList<>();

        // Convert x and y values into Point objects
        for (int i = 0; i < splitValuesX.length; i++) {
            Point point = new Point(Integer.parseInt(splitValuesX[i])/2, Integer.parseInt(splitValuesY[i])/2); //크기를 반으로 줄이기 때문에 2를 나눠야함
            nodeArrayList.add(point);
        }
    }

    ValueEventListener postListener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) snapshot.getValue();

            address = getStringValue(hashMap, "address");
            floorNum = getStringValue(hashMap, "floorNum");

            // Output or perform desired operations with the extracted values
            Log.d("Firebase", "address: " + address);
            Log.d("Firebase", "floorNum: " + floorNum);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.w("loadPost:onCancelled", error.toException());
        }
    };

    public String getBuildingName() {
        return buildingName;
    }

    public String getFloorNum() {
        return floorNum;
    }

    public double dijkstra(double[][] graph, int startNode, int endNode) {
        String tag = "Dijkstra";
        Log.d(tag, "No path found.");
        int numNodes = graph.length;
        boolean[] visited = new boolean[numNodes];
        double[] distance = new double[numNodes];
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

        return distance[endNode];
    }

    private static int getMinDistanceNode(double[] distance, boolean[] visited) {
        double minDistance = INFINITY;
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

    public void logShortestPath(int startNode, int endNode, int[] previous) {
        String tag = "Dijkstra";

        if (previous[endNode] == -1) {
            Log.d(tag, "No path found.");
        } else {
            ArrayList<Integer> path = new ArrayList<>();
            int node = endNode;

            while (node != startNode) {
                node = previous[node];
                path.add(0, node);
            }
            path.add(endNode);
            path.remove(0);

            this.pathIndex = path;
        }
    }

    public String getId() {
        return id;
    }

    public String getNodeNum() {
        return nodeNum;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getNode() {
        return node;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public ArrayList<Point> getNodeArrayList() {
        return nodeArrayList;
    }

    public ArrayList<Integer> getPathIndex() {
        return pathIndex;
    }
}
