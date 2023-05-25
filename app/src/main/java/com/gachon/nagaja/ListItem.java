package com.gachon.nagaja;

public class ListItem {
    private String name;
    private String address;
    private String imageName;

    ListItem(String name, String address,String imageName){
        this.name = name;
        this.address = address;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
