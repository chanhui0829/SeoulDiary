package com.example.travel;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelperMapMemo extends SQLiteOpenHelper {

    public DBHelperMapMemo(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE mapmemotb (mapnum INTEGER, num INTEGER, title TEXT, memo TEXT, priority TEXT, date TEXT, bitmap TEXT, x DOUBLE, y DOUBLE);");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS mapmemo");
        onCreate(db);
    }

}


