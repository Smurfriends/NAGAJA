package com.gachon.nagaja;

public class MapDTO {

    public int id;
    public String x;
    public String y;
    public String nodeNum;
    public String node;
    public String mapURL;


    public MapDTO(int fileId, String mapURL) {
        this.id = fileId;
        this.mapURL = mapURL;
        nodeNum = "";
        x = "";
        y = "";
        node = "";
    }

//    public void setMapURL(String mapURL) {
//        this.mapURL = mapURL;
//    }

    public String getMapURL(String mapURL) {
        return mapURL;
    }

}
