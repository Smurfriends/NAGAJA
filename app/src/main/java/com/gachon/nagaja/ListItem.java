package com.gachon.nagaja;

public class ListItem {
    private String BuildingName;
    private String floorNum;
    private String id;

    ListItem(String BuildingName, String floorNum, String id) {
        this.BuildingName = BuildingName;
        this.floorNum = floorNum;
        this.id = id;
    }

    public String getBuildingName() {
        return BuildingName;
    }

    public String getFloorNum() {
        return floorNum;
    }

    public String getId() {
        return id;
    }

}
