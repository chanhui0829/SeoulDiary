package com.example.travel;

public class MyMarkerItem {
    String title, memo,  date, bitmap, check;
    double x, y;
    int num;

    public MyMarkerItem(int num, String title, String memo, String date,String check, double x, double y, String bitmap){
        this.num=num;
        this.title=title;
        this.memo=memo;
        this.date=date;
        this.check=check;
        this.x=x;
        this.y=y;
        this.bitmap=bitmap;
    }
}
