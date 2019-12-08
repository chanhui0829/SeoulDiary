package com.example.travel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.travel.ListViewSize.setListViewHeightBasedOnChildren;

import static com.example.travel.RecommendSearchMapActivity.conItems2;
import static com.example.travel.RecommendSearchMapActivity.conjsonArray2;
import static com.example.travel.RecommendSearchMapActivity.end_X;
import static com.example.travel.RecommendSearchMapActivity.end_Y;
import static com.example.travel.RecommendSearchMapActivity.start_X;
import static com.example.travel.RecommendSearchMapActivity.start_Y;


public class MyItemAdapter2 extends BaseAdapter {
    Context context;
    ArrayList<MyItem> myItems;
    int layout;
    LayoutInflater inflater;
    ListView listView2;
    public MyItemAdapter2(Context context, ArrayList<MyItem> myItems, int layout){
        this.context = context;
        this.myItems = myItems;
        this.layout = layout;
        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return myItems.size();
    }

    @Override
    public Object getItem(int position) {
        return myItems.get(position).pay;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(layout, parent, false);
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        TextView tv = convertView.findViewById(R.id.item_money);
        tv.setText(""+myItems.get(position).pay+"원");
        TextView tv2 = convertView.findViewById(R.id.item_distan);
        tv2.setText("도보거리 : "+myItems.get(position).distance+"m");
        int minute = (myItems.get(position).time)%60;
        int hour = (myItems.get(position).time/60);
        String t="";
        if(hour!=0){
            t=""+hour+"시간 ";
        }
        TextView tv3 = convertView.findViewById(R.id.item_time);
        tv3.setText(t+minute+"분");

        final int posi=position;
        listView2 = convertView.findViewById(R.id.changelist);
        ChangeItemAdapter adapter = new ChangeItemAdapter(context,myItems.get(position).root,R.layout.changeitem);
        listView2.setAdapter(adapter);
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    Intent intent = new Intent(view.getContext(),SearchMapDetailActivity.class);
                    String mapObj = conItems2.get(position).mapObj;
                    String passStopList;

                    JSONObject subJsonObject = conjsonArray2.getJSONObject(conItems2.get(posi).position);
                    passStopList = subJsonObject.toString();

                    intent.putExtra("mapObj", mapObj);
                    intent.putExtra("passStopList", passStopList);
                    intent.putExtra("startXY", start_X+" "+start_Y);
                    intent.putExtra("endXY", end_X+" "+end_Y);

                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        setListViewHeightBasedOnChildren(listView2);


        return convertView;
    }


}
