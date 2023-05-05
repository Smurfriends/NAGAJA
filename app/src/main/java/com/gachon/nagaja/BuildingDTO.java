package com.gachon.nagaja;


public class BuildingDTO{

    // TODO: proguard-rules.pro 파일에 추가해도 오류가 안 고쳐져서 임시방편으로 public 씀
    // https://stackoverflow.com/questions/37743661/firebase-no-properties-to-serialize-found-on-class
    public String buildingName;
    public int floorUndergroundNum;
    public int floorUpgroundNum;
    // TODO: 층별 이름의 list (String)
    // TODO: 외부출입로가 있는 층의 list (String or int)
    // TODO: timestamp

    public BuildingDTO() {}
    public BuildingDTO(String buildingName, int floorUndergroundNum, int floorUpgroundNum) {
        this.buildingName = buildingName;
        this.floorUndergroundNum = floorUndergroundNum;
        this.floorUpgroundNum = floorUpgroundNum;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setFloorUndergroundNum(int floorUndergroundNum) {
        this.floorUndergroundNum = floorUndergroundNum;
    }

    public void setFloorUpgroundNum(int floorUpgroundNum) {
        this.floorUpgroundNum = floorUpgroundNum;
    }

    public String getBuildingName(String buildingName) {
        return buildingName;
    }

    public int getFloorUndergroundNum(int floorUndergroundNum) {
        return floorUndergroundNum;
    }

    public int getFloorUpgroundNum(int floorUpgroundNum) {
        return floorUpgroundNum;
    }

    
}
