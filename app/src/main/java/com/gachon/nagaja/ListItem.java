package com.gachon.nagaja;

public class ListItem {
    private String name;
    private String address;

    ListItem(String name, String address){
        this.name = name;
        this.name = address;
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
