package com.gachon.nagaja;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

public class CanvasView extends View {

    Paint paint = new Paint();
    public static int curEdit = 0;   // 지금 선택해서 좌표 수정 중인 node의 index
    int[] curEditTwo = {-1,-1};
    int count = 0;  // for drag
    int curDrag = -1; // 라인 위에서만 좌표 위치 선택하게 할 때 사용

    public ArrayList<Point> node_corner = new ArrayList<>();
    public ArrayList<NewNodeData> node_exit = new ArrayList<>();
    public ArrayList<double[][]> matrix = new ArrayList<>();
    public int nodeNum;
    private static final double MAX = 50000;

    public CanvasView(Context context, FindPath findPath) {
        super(context);

        // paint 기본 설정
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);

        node_corner = findPath.getNodeArrayList();
        matrix = findPath.getMatrix();
        nodeNum = findPath.getNodeNum();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        float density = getResources().getDisplayMetrics().density;

        // edge 그리기 (코너 node 받을 때는 안그려지게)
        if (curEdit == -2 || curEdit == -3) {
            paint.setColor(Color.rgb(255,228,225)); // 분홍색

            double[][] temp = matrix.get(0);

            for (int row = 0; row < temp.length; row++) {
                for (int col = 0; col < temp.length; col++) {
                    if (temp[row][col] != 0 && temp[row][col] != MAX) {
                        if ((row == curEditTwo[0] && col == curEditTwo[1]) || (row == curEditTwo[1] && col == curEditTwo[0])) {
                            paint.setColor(Color.rgb(152, 251, 152)); // 연두색
                        } else { paint.setColor(Color.rgb(255,228,225)); }  // 분홍색
                        canvas.drawLine(node_corner.get(row).x *density, node_corner.get(row).y *density, node_corner.get(col).x *density, node_corner.get(col).y *density, paint);
                    }   // 두 번 그려지긴 하는데 별 상관 없음
                }
            }
        }

        // node 그리기
        for (int i = 0; i < node_corner.size(); i++) {
            if (i == curEdit || i == curEditTwo[0] || i == curEditTwo[1]) {
                paint.setColor(Color.GREEN);
                if (curEdit == -3) {
                    paint.setColor(Color.rgb(152, 251, 152)); // 연두색
                }
            }
            else if (curEdit == -3) {
                paint.setColor(Color.rgb(255,228,225)); // 분홍색
            }
            else {
                paint.setColor(Color.RED);
            }
            canvas.drawCircle(node_corner.get(i).x *density,node_corner.get(i).y *density,5, paint);
        }

        // exit node 그리기
        for (int i = 0; i < node_exit.size(); i++) {
            if (i == curDrag) {
                paint.setColor(Color.rgb(0,150,0)); // darkgreen
            }
            else if (curEdit == -4) {
                paint.setColor(Color.rgb(255,228,225)); // 분홍색
            }
            else {
                paint.setColor(Color.RED);  // curEdit == -3
            }
            canvas.drawCircle(node_exit.get(i).x *density,node_exit.get(i).y *density,5, paint);
        }

    }

    // node 편집을 위한 터치 이벤트 처리 
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //터치 up이 되었을 때 화면 갱신
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                
                // get touch coordinate
                float[] point = new float[] {event.getX(), event.getY()};
                Log.d("Touch", "변환 전: ("+point[0] + " , " + point[1] + ")");

                // match with image
                float density = getResources().getDisplayMetrics().density;
                point[0] /= density;
                point[1] /= density;
                Log.d("Point", "변환 후: ("+point[0] + " , " + point[1] + ")");

                // find shortest Euclidean distance (비교용이라 굳이 루트 안 씌움)
                double min = 1000000000;
                int temp = 0;
                for (int i = 0; i < node_corner.size(); i++) {
                    if (min > (Math.pow(node_corner.get(i).x - point[0], 2) + Math.pow(node_corner.get(i).y - point[1], 2))) {
                        min = Math.pow(node_corner.get(i).x - point[0], 2) + Math.pow(node_corner.get(i).y - point[1], 2);
                        temp = i;
                    }
                }

                // 터치한 좌표에서 가장 가까운 node를 curEdit 혹은 curEditTwo로 설정
                if (curEdit > -2) {    // corner edit할 때 들어온 터치
                    curEdit = temp;
                    Log.d("curEditOne", "curEdit: "+ curEdit);
                }
                else if (curEdit == -2 || curEdit == -3) { // Hallway or Exit
                    if (curEditTwo[0] == -1) {
                        curEditTwo[0] = temp;
                    }
                    else if (curEditTwo[0] != temp && curEditTwo[1] == -1) {
                        curEditTwo[1] = temp;
                    }
                    Log.d("curEditTwo", "curEditTwo[0]: "+ curEditTwo[0] + " curEditTwo[1]: "+ curEditTwo[1]);
                }
                
                break;

            case MotionEvent.ACTION_MOVE:
                if (curEdit > -2) {    // corner edit할 때 들어온 드래그 이벤트
                    count++;

                    if (count > 8) {   // 너무 민감하지 않게 딜레이 주기. 그냥 터치할 때는 좌표 변하지 않도록
                        // get touch coordinate
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        // match with image
                        density = getResources().getDisplayMetrics().density;
                        x /= density;
                        y /= density;

                        // set node coordinate
                        node_corner.set(curEdit, new Point(x, y));
                        Log.d("now drag", "point: "+ node_corner.get(curEdit));
                        invalidate();
                    }
                }
                else if (curEdit == -3) {    // exit 노드 입력 받을 때 들어온 드래그
                    if (curDrag > -1) { // 드래그 모드라면
                        // get touch coordinate
                        int x = (int) event.getX();
                        int y = (int) event.getY();

                        // match with image
                        density = getResources().getDisplayMetrics().density;
                        x /= density;
                        y /= density;

                        // get node data to temp
                        NewNodeData tempNew = node_exit.get(curDrag);

                        // 이제 수학
                        if (tempNew.coefficient == 0 && tempNew.constant == 0) {
                            // x값은 고정. 받아 온 y 좌표를 따름
                            node_exit.set(curDrag, new NewNodeData(curEditTwo[0], curEditTwo[1], node_corner.get(curEditTwo[0]).x, y, tempNew.coefficient, tempNew.constant));
                        }
                        else if (tempNew.coefficient == 0) {
                            // y값은 고정. 받아 온 x 좌표를 따름
                            node_exit.set(curDrag, new NewNodeData(curEditTwo[0], curEditTwo[1], x, node_corner.get(curEditTwo[0]).y, tempNew.coefficient, tempNew.constant));
                        }
                        else if (tempNew.coefficient >= 1 || tempNew.coefficient <= -1) {   // 기울기가 가파를 때
                            // 직선 방정식에, 받아 온 y 좌표를 대입해서 x를 변환
                            x = (int) ((y - tempNew.constant) / tempNew.coefficient);
                            node_exit.set(curDrag, new NewNodeData(curEditTwo[0], curEditTwo[1], x, y, tempNew.coefficient, tempNew.constant));
                        }
                        else if (tempNew.coefficient <= 1 || tempNew.coefficient >= -1) {   // 기울기가 완만할 때
                            // 직선 방정식에, 받아 온 x 좌표를 대입해서 y를 변환
                            y = (int) (tempNew.coefficient * x + tempNew.constant);
                            node_exit.set(curDrag, new NewNodeData(curEditTwo[0], curEditTwo[1], x, y, tempNew.coefficient, tempNew.constant));
                        }

//
                        invalidate();
//                        Log.d("exitNode x", String.valueOf(node_exit.get(curDrag).x));
//                        Log.d("exitNode y", String.valueOf(node_exit.get(curDrag).y));
//                        Log.d("exitNode coefficient", String.valueOf(node_exit.get(curDrag).coefficient));
//                        Log.d("exitNode constant", String.valueOf(node_exit.get(curDrag).constant));
                    }
                }
                break;
            
            case MotionEvent.ACTION_UP :
                count = 0;  // 드래그 카운트 초기화
                invalidate();
                break;
        }

        return true;
    }

    // 좌표 상하좌우 이동 버튼에서 호출하는 함수
    public void moveNodeCoordinate(String direction) {

        // 현재 수정 중인 node 선언
        Point temp = node_corner.get(curEdit);

        // 받은 노드의 좌표를 조정
        switch (direction) {
            case "up" :
                temp.y = temp.y - 1;
                node_corner.set(curEdit, new Point(temp));
                break;
            case "down" :
                temp.y = temp.y + 1;
                node_corner.set(curEdit, new Point(temp));
                break;
            case "left" :
                temp.x = temp.x - 1;
                node_corner.set(curEdit, new Point(temp));
                break;
            case "right" :
                temp.x = temp.x + 1;
                node_corner.set(curEdit, new Point(temp));
                break;
        }

        // 다시 그리기
        invalidate();
    }

    // 추가한 노드를 반영하기 위해 matrix 사이즈 늘리기
    public void setMatrixAfterAddCornerNode() {
        
        int originSize = matrix.get(0).length;
        double[][] temp = new double[originSize + 1][originSize + 1];   // 사이즈 하나 늘리기
        
        // nodeNum 자리에 새로 초기화해서 넣고 exit노드의 matrix는 갈라져서 밀기
        for (int i = 0; i < originSize + 1; i++) {
            for (int j = 0; j < originSize + 1; j++) {

                if (i == nodeNum && j == nodeNum) { // 본인
                    temp[i][j] = 0;
                }
                else if (i == nodeNum || j == nodeNum) {    // 본인과는 모두 연결 안되어있음
                    temp[i][j] = MAX;
                }
                else if (i < nodeNum && j < nodeNum) {  // 2사분면
                    temp[i][j] = matrix.get(0)[i][j];
                }
                else if (i < nodeNum) { // && j >= curEdit  // 1사분면
                    temp[i][j] = matrix.get(0)[i][j-1];
                }
                else if (j < nodeNum) { // && i >= curEdit  // 3사분면
                    temp[i][j] = matrix.get(0)[i-1][j];
                }
                else {    // i >= curEdit && j >= curEdit   // 4사분면
                    temp[i][j] = matrix.get(0)[i-1][j-1];
                }
            }
        }
        
       // nodeNum update
        nodeNum = nodeNum + 1;

        // matrix update
        matrix.set(0, temp);
    }

    // 삭제한 노드를 반영하기 위해 matrix 사이즈 줄이고 연결 바꾸기
    public void setMatrixAfterDeleteCornerNode() {
        
        int originSize = matrix.get(0).length;
        double[][] temp = new double[originSize - 1][originSize - 1];   // 사이즈 하나 줄이기

        // curEdit 부분을 제외하고 matrix 재구성
        for (int i = 0; i < originSize - 1; i++) {
            for (int j = 0; j < originSize - 1; j++) {

                if (i < curEdit && j < curEdit) {
                    temp[i][j] = matrix.get(0)[i][j];
                }
                else if (i < curEdit) { // && j >= curEdit
                    temp[i][j] = matrix.get(0)[i][j+1];
                }
                else if (j < curEdit) { // && i >= curEdit
                    temp[i][j] = matrix.get(0)[i+1][j];
                }
                else {    // i >= curEdit && j >= curEdit
                    temp[i][j] = matrix.get(0)[i+1][j+1];
                }
            }
        }

        // nodeNum update
        if (curEdit < nodeNum) {
            nodeNum = nodeNum - 1;
        }

        // matrix update
        matrix.set(0, temp);
    }

    // corner 노드 위치가 바뀐 이후 가중치 재계산
    public void recalculateMatrixWeights() {
        double[][] temp = matrix.get(0);

        for (int row = 0; row < temp.length; row++) {
            for (int col = 0; col < temp.length; col++) {
                if (temp[row][col] != 0 && temp[row][col] != MAX) {   // 연결되어 있다면
                    double weight = Math.sqrt(Math.pow(node_corner.get(row).x - node_corner.get(col).x, 2) + (Math.pow(node_corner.get(row).y - node_corner.get(col).y, 2)));
                    temp[row][col] = weight;
                }
            }
        }

        matrix.set(0, temp);
    }

    // Hallway edit할 때 라인 connect/disconnect 하는 함수
    public void editConnection() {
        int a = curEditTwo[0];
        int b = curEditTwo[1];

        double[][] temp = matrix.get(0);

        if (temp[a][b] == MAX) {    // connect
            double weight = Math.sqrt(Math.pow(node_corner.get(a).x - node_corner.get(b).x, 2) + (Math.pow(node_corner.get(a).y - node_corner.get(b).y, 2)));

            temp[a][b] = weight;
            temp[b][a] = weight;
        }
        else {  // disconnect
            temp[a][b] = MAX;
            temp[b][a] = MAX;
        }

        // apply edit result
        matrix.set(0, temp);

        // deselect
        curEditTwo[0] = -1;
        curEditTwo[1] = -1;

        invalidate();
    }

    // Exit node를 각 arraylist에 추가하는 함수
    public void addExitNode() {

        if (curEditTwo[0] != -1 && curEditTwo[1] != -1) {

            double[][] temp = matrix.get(0);
            if (temp[curEditTwo[0]][curEditTwo[1]] != MAX) {

                // 중간 좌표 계산
                int newX = (node_corner.get(curEditTwo[0]).x + node_corner.get(curEditTwo[1]).x) / 2;
                int newY = (node_corner.get(curEditTwo[0]).y + node_corner.get(curEditTwo[1]).y) / 2;

                // 직선의 방정식 도출
                int differenceX = node_corner.get(curEditTwo[0]).x - node_corner.get(curEditTwo[1]).x;
                int differenceY = node_corner.get(curEditTwo[0]).y - node_corner.get(curEditTwo[1]).y;

                float coefficient;
                float constant;
                if (differenceX == 0) { coefficient = 0; constant = 0; }  // x=1 이런 형태
                else if (differenceY == 0) { coefficient = 0; constant = node_corner.get(curEditTwo[0]).y; }    // y=2 이런 형태
                else {  // y=ax+b 기본 형태
                    coefficient = (float) differenceY / (float) differenceX;
                    constant = node_corner.get(curEditTwo[1]).y - (coefficient * node_corner.get(curEditTwo[1]).x);
                }

                // exit node 생성
                node_exit.add(new NewNodeData(curEditTwo[0], curEditTwo[1], newX, newY, coefficient, constant));
                curDrag = node_exit.size() - 1;  // 이제 드래그로 조정 가능

                // 다시 그리기
                invalidate();
            }
        }
    }

    // Exit node 정보를 matrix에 추가
    public void addEdgeOfExitNodeToMatrix() {
        double[][] cornerMatrix = matrix.get(0);   // 원래 매트릭스 가져오기
        int size = node_corner.size() + node_exit.size();
        double[][] finalMatrix = new double[size][size];

        // finalMatrix 초기화
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) { finalMatrix[i][j] = 0; }
                else { finalMatrix[i][j] = MAX; }
            }
        }

        // 원래 매트릭스를 finalMatrix에 일단 넣기 (추가된 공간은 현재 0)
        for (int i = 0; i < node_corner.size(); i++) {
            for (int j = 0; j < node_corner.size(); j++) {
                finalMatrix[i][j] = cornerMatrix[i][j];
            }
        }

        // exit node의 edge 추가하기
        for (int i = 0; i < node_exit.size(); i++) {
            // 가중치 계산
            int weight1 = (int) Math.sqrt(Math.pow(node_corner.get(node_exit.get(i).node1).x - node_exit.get(i).x, 2) + Math.pow(node_corner.get(node_exit.get(i).node1).y - node_exit.get(i).y, 2));
            int weight2 = (int) Math.sqrt(Math.pow(node_corner.get(node_exit.get(i).node2).x - node_exit.get(i).x, 2) + Math.pow(node_corner.get(node_exit.get(i).node2).y - node_exit.get(i).y, 2));

            if (finalMatrix[node_exit.get(i).node1][node_exit.get(i).node2] == MAX) { // 그 간선에 이미 누가 있어서 원래 연결이 끊겼다면 (같은 간선에 exit node는 2개까지만 가능하게 짰음)
                for (int j = 0; j < i; i++) {   // 범인 찾기
                    if (finalMatrix[node_exit.get(i).node1][j + node_corner.size()] != MAX && finalMatrix[node_exit.get(i).node2][j + node_corner.size()] != MAX) {
                        // 나는 i, 범인은 j
                        // 서로의 node1과 node2가 반대일 수도 있으니까 맞추기
                        if (node_exit.get(i).node1 != node_exit.get(j).node1) {
                            NewNodeData tmp = node_exit.get(i);
                            node_exit.set(i, new NewNodeData(tmp.node2, tmp.node1, tmp.x, tmp.y, tmp.coefficient, tmp.constant));
                            int tmpWeight = weight1;
                            weight1 = weight2;
                            weight2 = tmpWeight;
                        }

                        // j와의 거리 계산
                        int newWeight = (int) Math.sqrt(Math.pow(node_exit.get(j).x - node_exit.get(i).x, 2) + Math.pow(node_exit.get(j).y - node_exit.get(i).y, 2));

                        // 가중치 비교
                        if (weight1 < finalMatrix[node_exit.get(j).node1][j + node_corner.size()]) { // 내가 node1이랑 더 가까우면
                            // node1과 j의 연결 끊기
                            finalMatrix[node_exit.get(j).node1][j + node_corner.size()] = MAX;
                            finalMatrix[j + node_corner.size()][node_exit.get(j).node1] = MAX;

                            // 나를 node1과 j 사이에 새로 연결
                            finalMatrix[node_exit.get(i).node1][i + node_corner.size()] = weight1;
                            finalMatrix[i + node_corner.size()][node_exit.get(i).node1] = weight1;
                            finalMatrix[j + node_corner.size()][i + node_corner.size()] = newWeight;
                            finalMatrix[i + node_corner.size()][j + node_corner.size()] = newWeight;
                        }
                        else {  // 내가 node2랑 더 가까우면
                            // node2과 j의 연결 끊기
                            finalMatrix[node_exit.get(j).node2][j + node_corner.size()] = MAX;
                            finalMatrix[j + node_corner.size()][node_exit.get(j).node2] = MAX;

                            // 나를 node2과 j 사이에 새로 연결
                            finalMatrix[node_exit.get(i).node2][i + node_corner.size()] = weight1;
                            finalMatrix[i + node_corner.size()][node_exit.get(i).node2] = weight1;
                            finalMatrix[j + node_corner.size()][i + node_corner.size()] = newWeight;
                            finalMatrix[i + node_corner.size()][j + node_corner.size()] = newWeight;
                        }
                    }
                }
            }
            else {   // 그 간선에 첫 입장
                // 원래 연결 끊기
                finalMatrix[node_exit.get(i).node1][node_exit.get(i).node2] = MAX;
                finalMatrix[node_exit.get(i).node2][node_exit.get(i).node1] = MAX;

                // 나를 node1과 node2 사이에 새로 연결
                finalMatrix[node_exit.get(i).node1][i + node_corner.size()] = weight1;
                finalMatrix[i + node_corner.size()][node_exit.get(i).node1] = weight1;
                finalMatrix[node_exit.get(i).node2][i + node_corner.size()] = weight2;
                finalMatrix[i + node_corner.size()][node_exit.get(i).node2] = weight2;
            }
        }
        
        // finalMatrix를 matrix에 넣기
        matrix.set(0, finalMatrix); // index 0에 세팅
        

    }

    // Exit node를 Corner node 리스트에 합치기
    public void combineExitNodeToCornerNodeList() {
        int sizeBeforeCombine = node_corner.size();   // 원래 코너 노드 개수. isEndNode=true 가 되는 시작점으로 쓰기 위해 return

        for (int i = 0; i < node_exit.size(); i++) {
            node_corner.add(new Point(node_exit.get(i).x, node_exit.get(i).y));
            Log.d("combineExitNodeIndex", String.valueOf(i));
            Log.d("combineExitNode", String.valueOf(node_exit.get(i).x));
            Log.d("combineExitNode", String.valueOf(node_exit.get(i).y));
        }
    }

    public class NewNodeData {
        int node1, node2, x, y;
        float coefficient, constant;

        public NewNodeData(int node1, int node2, int x, int y, float coefficient, float constant) {

            this.node1 = node1;
            this.node2 = node2;
            this.x = x;
            this.y = y;
            this.coefficient = coefficient; // 방정식의 계수
            this.constant = constant;   // 방정식의 상수
        }
    }

}

