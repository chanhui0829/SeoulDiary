package com.example.travel;

public class DetailItem {
    String name, number, replace, way, count, stationID;
    int type, bustype;
    double x, y;

    public DetailItem(int type, String number, String name, String replace, String way,String count, double x, double y, int bustype, String stationID){
        this.type = type;
        this.number = number;
        this.name = name;
        this.replace=replace;
        this.way=way;
        this.count=count;
        this.x=x;
        this.y=y;
        this.bustype=bustype;
        this.stationID = stationID;
    }
}
