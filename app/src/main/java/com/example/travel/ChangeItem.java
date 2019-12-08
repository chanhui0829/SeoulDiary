package com.example.travel;

public class ChangeItem {
    String name, number, replace;
    int type;

    public ChangeItem(int type, String number, String name, String replace){
        this.type = type;
        this.number = number;
        this.name = name;
        this.replace=replace;
    }
}
