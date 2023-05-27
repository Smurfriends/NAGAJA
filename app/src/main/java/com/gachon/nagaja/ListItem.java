package com.gachon.nagaja;

public class ListItem {
    private String BuildingName;
    private String floorNum;
    private String id;
    private String nodeNum;
    private String x;
    private String y;
    private String node;
    private String bname;

    public ListItem(String bname,String BuildingName, String floorNum, String id, String nodeNum, String x, String y, String node) {
        this.bname = bname;
        this.BuildingName = BuildingName;
        this.floorNum = floorNum;
        this.id = id;
        this.nodeNum = nodeNum;
        this.x = x;
        this.y = y;
        this.node = node;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public void setBuildingName(String BuildingName) {
        this.BuildingName = BuildingName;
    }

    public String getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(String floorNum) {
        this.floorNum = floorNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeNum() {
        return nodeNum;
    }

    public void setNodeNum(String nodeNum) {
        this.nodeNum = nodeNum;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getBname() {
        return bname;
    }
}
