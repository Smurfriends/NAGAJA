package com.gachon.nagaja;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class RouteCanvasView extends View {

    Paint paint = new Paint();
    float density = getResources().getDisplayMetrics().density;
    int count = 0;  // for drag
    public boolean showPath = false;   // drawPath 보여줄지말지
    
    // 시작노드를 위한 변수들
    public Point curLocation = new Point();    // 현 위치 터치 좌표
    public Point curLocationNode = new Point();    // 현 위치 노드 좌표 (수선의 발)
    public int node1;    // node1과 node2 사이에 curLocationNode가 위치. (index값 저장)
    public int node2;

    // TODO: 파베에서 정보 받아오는 코드 넣고 나면, 아래에 있는 테스트용 초기화 정보 지우고 선언만 남기기
//    public static ArrayList<Point> node = new ArrayList<>(
//            Arrays.asList(new Point(20,60),new Point(100,60),new Point(20,300), new Point(20,600), new Point(130,300), new Point(160,600), new Point(180,600))
//    );  // 테스트 용으로 초기화 값 넣어둠
    public ArrayList<Point> node = new ArrayList<>(); //원래는 이런식으로만
    public ArrayList<double[][]> matrix = new ArrayList<>();    // 공간은 하나만 씀. 매번 배열 크기를 다르게 써야해서 사용
    public ArrayList<Integer> pathIndex = new ArrayList<>();

    private FindPath findPath;
    public RouteCanvasView(Context context, FindPath findPath) { //findPath에서 가져오면 안됌 이름을 넘겨받고 데이터를 받아오는게 나음
        super(context);

        // paint 기본 설정
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);

        this.findPath = findPath;

//        matrix.add(tempMatrix); // 임시로. 나중에 삭제

    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (showPath == true) {

            Path path = new Path();

            // 현위치부터 시작 노드좌표까지는 drawLine
            canvas.drawLine(curLocation.x *density, curLocation.y *density,
                    curLocationNode.x *density,curLocationNode.y *density, paint);

            // 노드좌표부터 drawPath
            path.moveTo(curLocationNode.x *density,curLocationNode.y *density); // 출발지 좌표

            // path 저장
            for (int i = 0; i < pathIndex.size(); i++) {
                path.lineTo(node.get(pathIndex.get(i)).x * density, node.get(pathIndex.get(i)).y * density);
            }
            canvas.drawPath(path, paint);   // draw

            // 도착지 동그라미
//            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.exit);  // 아이콘
            canvas.drawCircle(node.get(pathIndex.get(pathIndex.size()-1)).x *density,
                    node.get(pathIndex.get(pathIndex.size()-1)).y *density, 5, paint);
        }

        // 현위치 좌표 그리기
        paint.setColor(Color.RED);
        canvas.drawCircle(curLocation.x*density,curLocation.y*density,10, paint);

        // node 그리기 // test용
        paint.setColor(Color.rgb(255,228,225)); // 분홍색
        for (int i = 0; i < node.size(); i++) {
            canvas.drawCircle(node.get(i).x *density,node.get(i).y *density,5, paint);
        }

        // node 그리기 // test용
        paint.setColor(Color.GREEN);
        canvas.drawCircle(node.get(node1).x *density,node.get(node1).y *density,5, paint);
        canvas.drawCircle(node.get(node2).x *density,node.get(node2).y *density,5, paint);

        paint.setColor(Color.rgb(0,150,0)); // darkgreen
        canvas.drawCircle(curLocationNode.x *density,curLocationNode.y *density,5, paint);


    }

    // node 편집을 위한 터치 이벤트 처리 
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (showPath == false) { // 현재 위치 좌표를 받는 화면에서만 실행

            //터치 up이 되었을 때 화면 갱신
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:

                    count++;
                    if (count > 8) {   // 너무 민감하지 않게 딜레이 주기. 그냥 터치할 때는 좌표 변하지 않도록
                        // get touch coordinate
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        // match with image
                        x /= density;
                        y /= density;

                        // 현 위치 좌표 세팅
                        curLocation.x = x;
                        curLocation.y = y;
                        setStartNode(); // test
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    count = 0;  // 드래그 카운트 초기화

                    setStartNode(); // 시작 노드 설정
                    addEdgeOfStartNodeToMatrix();   // 시작 노드의 edge 2개를 matrix에 추가
                    findShortestPathToAllExits(4);    // exit 노드 개수만큼 다익스트라 //firstExitNodeIndex. nodeNum
                    showPath = true;    // 다음 화면에서 drawPath 코드 실행되도록

                    invalidate();
                    break;
            }
        }
        return true;
    }

    // 시작 노드의 터치 좌표를 바탕으로 노드 좌표(수선의 발), node1, node2를 구하는 함수
    public void setStartNode() {

        double[][] temp = matrix.get(0);   // edge matrix
        ArrayList<int[]> minEdge = new ArrayList<>(); // int[4] 안에 node1, node2, footX, footY 임시 저장

        int footX = 0;  // 수선의 발
        int footY = 0;
        double lengthOfPerpendicular = 0;  // 수선의 길이

        double min = 100000;
        for (int row = 0; row < node.size(); row++) {
            for (int col = 0; col < node.size(); col++) {
                if (row != col && temp[row][col] != 100000) {   // 연결 되어 있다면
                    // 수선의 발 & 수선의 길이 (점과 직선 사이의 거리) 구하기

                    int differenceX = node.get(row).x - node.get(col).x;    // x2-x1 (row가 x2,y2)
                    int differenceY = node.get(row).y - node.get(col).y;    // y2-y1 (column이 x1,y1)

                    if (differenceX == 0) {   // x=a 형태
                        footX = node.get(row).x;
                        footY = curLocation.y;
                        lengthOfPerpendicular = Math.abs(curLocation.x - node.get(row).x);
                    }
                    else if (differenceY == 0) {  // y=a 형태
                        footX = curLocation.x;
                        footY = node.get(row).y;
                        lengthOfPerpendicular = Math.abs(curLocation.y - node.get(row).y);
                    }
                    else {  // y=ax+b 형태
                        float coefficient = (float) differenceY / (float) differenceX;  // 방정식의 계수
                        float constant = node.get(col).y - (coefficient * node.get(col).x); // 방정식의 상수

                        float coefficientOfPerpendicular = (1 / coefficient) * -1;  // 수선의 방정식의 계수
                        float constantOfPerpendicular = curLocation.y - (curLocation.x * coefficientOfPerpendicular);  // 수선의 방정식의 상수

                        // 수선의 발 구하기
                        footX = (int) ((constantOfPerpendicular - constant) / (coefficient - coefficientOfPerpendicular));
                        footY = (int) ((coefficient * footX) + constant);

                        // 수선의 발과의 거리 구하기
                        lengthOfPerpendicular = Math.sqrt(Math.pow(curLocation.x - footX, 2) + Math.pow(curLocation.y - footY, 2));
                    }

                    // 최솟값을 저장
                    if (lengthOfPerpendicular <= min) {
                        min = lengthOfPerpendicular;
                        
                        // min이랑 같지 않고, 더 작은 최솟값이면 ArrayList 비우기
                        if (lengthOfPerpendicular < min) { minEdge.clear(); }

                        // minEdge index 0:node1, 1:node2, 2:curLocationNode.x, 3:curLocationNode.y
                        int[] data = {row, col, footX, footY};
                        minEdge.add(data);
                    }
                }
            }
        }

        // minEdge 리스트 중에서  node1, node2와의 유클리드 거리가 가장 짧은 것을 찾기
        min = 100000;
        for (int i = 0; i < minEdge.size(); i++) {
            // minEdge index 0:node1, 1:node2, 2:curLocationNode.x, 3:curLocationNode.y
            double distance1 = Math.sqrt(Math.pow(curLocation.x - node.get(minEdge.get(i)[0]).x, 2) + Math.pow(curLocation.y - node.get(minEdge.get(i)[0]).y, 2));
            double distance2 = Math.sqrt(Math.pow(curLocation.x - node.get(minEdge.get(i)[1]).x, 2) + Math.pow(curLocation.y - node.get(minEdge.get(i)[1]).y, 2));

            if ((distance1 + distance2) < min) {
                min = distance1 + distance2;

                // 시작 노드 data에 넣기
                node1 = minEdge.get(i)[0];
                node2 = minEdge.get(i)[1];
                curLocationNode.x = minEdge.get(i)[2];
                curLocationNode.y = minEdge.get(i)[3];

                Log.d("setStartNode", "x: " + curLocationNode.x+ " y: " + curLocationNode.y);
            }
        }
    }
//            int a = matrix.get(0)[2][3];  // 이거 이런 식으로 불러오기 가능한 거 이제 알았네 와 진짜 matrix.get().get() 막 이러고 있었는데... 진짜 감자다...


    // 시작 노드를 edge matrix에 추가하는 함수
    public void addEdgeOfStartNodeToMatrix() {
        // curLocationNode.x, curLocationNode.y를 가중치 계산 후 node1,node2 사이에 넣기.

        double[][] temp = matrix.get(0);   // 원래 매트릭스 가져오기
        double[][] pathMatrix = new double[node.size()+1][node.size()+1];

        // pathmatrix 초기화
        for (int i = 0; i < node.size() + 1; i++) {
            for (int j = 0; j < node.size() + 1; j++) {
                if (i == j) { pathMatrix[i][j] = 0; }
                else { pathMatrix[i][j] = 100000; }
            }
        }

        // 원래 매트릭스를 pathMatrix에 일단 넣기
        for (int i = 0; i < node.size(); i++) {
            for (int j = 0; j < node.size(); j++) {
                pathMatrix[i][j] = temp[i][j];
            }
        }

        // 가중치 계산
        int weight1 = (int) Math.sqrt(Math.pow(node.get(node1).x - curLocationNode.x, 2) + Math.pow(node.get(node1).y - curLocationNode.y, 2));
        int weight2 = (int) Math.sqrt(Math.pow(node.get(node2).x - curLocationNode.x, 2) + Math.pow(node.get(node2).y - curLocationNode.y, 2));

        // 원래 연결 끊기
        pathMatrix[node1][node2] = 100000;
        pathMatrix[node2][node1] = 100000;

        // 시작 노드의 edge 추가
        pathMatrix[node1][node.size()] = weight1;
        pathMatrix[node.size()][node1] = weight1;
        pathMatrix[node2][node.size()] = weight2;
        pathMatrix[node.size()][node2] = weight2;

        for (int i = 0; i < node.size() + 1; i++) {
            for (int j = 0; j < node.size() + 1; j++) {
                Log.d("pathMatrix", "" + pathMatrix[i][j]);
            }
        }
        Log.d("pathMatrix", "" + pathMatrix[0]);
        Log.d("pathMatrix", "" + pathMatrix[1]);
        Log.d("pathMatrix", "" + pathMatrix[2]);
        Log.d("pathMatrix", "" + pathMatrix[3]);
        Log.d("pathMatrix", "" + pathMatrix[4]);
        Log.d("pathMatrix", "" + pathMatrix[5]);
        Log.d("pathMatrix", "" + pathMatrix[6]);
        Log.d("pathMatrix", "" + pathMatrix[7]);
        
        matrix.add(pathMatrix); // index 1에 들어감
    }

    // exit 노드 개수만큼 다익스트라
    public void findShortestPathToAllExits(int firstIndexOfExitNode) {

        int shortestExit = -1;

        double min = Double.POSITIVE_INFINITY;
        for (int i = firstIndexOfExitNode; i < node.size(); i++) {
            double result = FindPath.dijkstra(matrix.get(1), node.size(), i);
            if (result < min) {
                min = result;
                shortestExit = i;
            }
        }

        // 가장 짧은 exit을 endNode로 하는 다익스트라를 다시 호출해서 저장되게 하기 (비효율적이지만 돌아는 감)
        FindPath.dijkstra(matrix.get(1), node.size(), shortestExit);

    }
//// 받아 온 좌표를 CanvasView에 있는 node_corner ArrayList에 넣기
//    routeCanvasView.pathIndex = findPath.getPath();
//    routeCanvasView.node = findPath.getNodeArrayList();
//        routeCanvasView.matrix.add(findPath.getMatrix());
}

