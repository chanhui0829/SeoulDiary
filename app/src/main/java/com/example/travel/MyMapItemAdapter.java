package com.example.travel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyMapItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<MyMapItem> myMapItems;
    int layout;
    LayoutInflater inflater;

    public MyMapItemAdapter(Context context, ArrayList<MyMapItem> myMapItems, int layout){
        this.context = context;
        this.myMapItems = myMapItems;
        this.layout = layout;

        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return myMapItems.size();
    }

    @Override
    public Object getItem(int position) {

        return myMapItems.get(position).mapnum;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(layout, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.mi_mapname);
        tv.setText(myMapItems.get(position).mapname);
        TextView tv1 = convertView.findViewById(R.id.mi_date);
        tv1.setText(myMapItems.get(position).mapdate);
        return convertView;
    }
}
