package com.example.travel;


import java.util.ArrayList;

public class MyItem {
    int position;
    String mapObj;
    int pay, time, distance;
    ArrayList<ChangeItem> root;

    public MyItem(int pay,String mapObj, int time, int distance, ArrayList<ChangeItem> root,int position){
        this.pay = pay;
        this.mapObj= mapObj;
        this.time = time;
        this.distance = distance;
        this.root = root;
        this.position = position;
    }

}
