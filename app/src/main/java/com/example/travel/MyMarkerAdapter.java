package com.example.travel;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.travel.ListViewSize.StringToBitmap;

public class MyMarkerAdapter extends BaseAdapter {
    Context context;
    ArrayList<MyMarkerItem> myMarkerItems;
    int layout;
    LayoutInflater inflater;

    public MyMarkerAdapter(Context context, ArrayList<MyMarkerItem> myMarkerItems, int layout){
        this.context = context;
        this.myMarkerItems = myMarkerItems;
        this.layout = layout;

        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return myMarkerItems.size();
    }

    @Override
    public Object getItem(int position) {

        return myMarkerItems.get(position).title;
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



        TextView mark_title = convertView.findViewById(R.id.mark_title);
        TextView mark_memo = convertView.findViewById(R.id.mark_memo);
        TextView mark_date = convertView.findViewById(R.id.mark_date);
        TextView mark_check = convertView.findViewById(R.id.mark_check);
        TextView mark_latlng = convertView.findViewById(R.id.mark_latlng);
        ImageView mark_iv = convertView.findViewById(R.id.mark_iv);
        Bitmap bb = StringToBitmap(myMarkerItems.get(position).bitmap);

        mark_title.setText(myMarkerItems.get(position).title);
        mark_memo.setText(myMarkerItems.get(position).memo);
        mark_date.setText(myMarkerItems.get(position).date);
        mark_check.setText(myMarkerItems.get(position).check);
        mark_latlng.setText("x:"+myMarkerItems.get(position).x+"y:"+myMarkerItems.get(position).y);
        mark_iv.setImageBitmap(bb);

        return convertView;
    }


}
