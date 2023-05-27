package com.gachon.nagaja;


public class BuildingDTO{

    // TODO: proguard-rules.pro 파일에 추가해도 오류가 안 고쳐져서 임시방편으로 public 씀
    // https://stackoverflow.com/questions/37743661/firebase-no-properties-to-serialize-found-on-class
    public String buildingName;
    public int floorOfMap;

    // TODO: 층별 이름의 list (String)
    // TODO: 외부출입로가 있는 층의 list (String or int)
    // TODO: timestamp

    public BuildingDTO() {}
    public BuildingDTO(String buildingName, int floorOfMap) {
        this.buildingName = buildingName;
        this.floorOfMap = floorOfMap;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public void setFloorOfMap(int floorOfMap) {
        this.floorOfMap = floorOfMap;
    }

    public int getFloorUndergroundNum(int floorOfMap) {
        return floorOfMap;
    }


    
}
