package com.example.travel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<DetailItem> DetailItems;
    int layout;
    LayoutInflater inflater;

    public DetailItemAdapter(Context context, ArrayList<DetailItem> DetailItems, int layout){
        this.context = context;
        this.DetailItems = DetailItems;
        this.layout = layout;
        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return DetailItems.size();
    }

    @Override
    public Object getItem(int position) {

        return DetailItems.get(position).type;
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


        TextView tv = convertView.findViewById(R.id.detail_name);

        TextView tv1 = convertView.findViewById(R.id.detail_distance);
        tv1.setText(DetailItems.get(position).count);
        TextView tv3 = convertView.findViewById(R.id.detail_num);

        ImageView iv = convertView.findViewById(R.id.detail_type);

        if(DetailItems.get(position).type==2){
            tv3.setText(DetailItems.get(position).number+"\n대체버스 : "+DetailItems.get(position).replace);
            iv.setImageResource(R.drawable.ic_bus);


            tv.setText(DetailItems.get(position).name+" 승차");
            iv.setColorFilter(Color.parseColor("#0d3692"), PorterDuff.Mode.SRC_IN);

        }else if(DetailItems.get(position).type==1){
            TextView tv2 = convertView.findViewById(R.id.detail_subtype);
            tv3.setText(DetailItems.get(position).way);
            iv.setImageResource(R.drawable.ic_train);
            tv.setText(DetailItems.get(position).name+" 승차");

            if(DetailItems.get(position).number.equals("100")){
                tv2.setText("분당선 ");
                iv.setColorFilter(Color.parseColor("#ff8c00"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ff8c00"));
            }else if(DetailItems.get(position).number.equals("101")) {
                tv2.setText("공항철도 ");
                iv.setColorFilter(Color.parseColor("#3681b7"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#3681b7"));
            }else if(DetailItems.get(position).number.equals("104")) {
                tv2.setText("경의중앙선 ");
                iv.setColorFilter(Color.parseColor("#73c7a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#73c7a6"));
            }else if(DetailItems.get(position).number.equals("107")) {
                tv2.setText("에버라인 ");
                iv.setColorFilter(Color.parseColor("#4ea346"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#4ea346"));
            }else if(DetailItems.get(position).number.equals("108")) {
                tv2.setText("경춘선 ");
                iv.setColorFilter(Color.parseColor("#32c6a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#32c6a6"));
            }else if(DetailItems.get(position).number.equals("102")) {
                tv2.setText("자기부상철도 ");
                iv.setColorFilter(Color.parseColor("#ffcd12"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ffcd12"));
            }else if(DetailItems.get(position).number.equals("109")) {
                tv2.setText("신분당선 ");
                iv.setColorFilter(Color.parseColor("#c82127"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#c82127"));
            }else if(DetailItems.get(position).number.equals("110")) {
                tv2.setText("의정부경전철 ");
                iv.setColorFilter(Color.parseColor("#fda600"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#fda600"));
            }else if(DetailItems.get(position).number.equals("111")) {
                tv2.setText("수인선 ");
                iv.setColorFilter(Color.parseColor("#ff8c00"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ff8c00"));
            }else if(DetailItems.get(position).number.equals("112")) {
                tv2.setText("경강선 ");
                iv.setColorFilter(Color.parseColor("#0054a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#0054a6"));
            }else if(DetailItems.get(position).number.equals("113")) {
                tv2.setText("우이신설선 ");
                iv.setColorFilter(Color.parseColor("#B0CE18"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#B0CE18"));
            }else if(DetailItems.get(position).number.equals("114")) {
                tv2.setText("서해선 ");
                iv.setColorFilter(Color.parseColor("#8FC31E"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#8FC31E"));
            }else if(DetailItems.get(position).number.equals("21")) {
                tv2.setText("인천 1호선 ");
                iv.setColorFilter(Color.parseColor("#8cadcb"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#8cadcb"));
            }else if(DetailItems.get(position).number.equals("22")) {
                tv2.setText("인천 2호선 ");
                iv.setColorFilter(Color.parseColor("#ed8000"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ed8000"));
            }else{

                tv2.setText(DetailItems.get(position).number+"호선 ");
                if(DetailItems.get(position).number.equals("1")){
                    iv.setColorFilter(Color.parseColor("#0d3692"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#0d3692"));
                }else if(DetailItems.get(position).number.equals("2")){
                    iv.setColorFilter(Color.parseColor("#33a23d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#33a23d"));
                }else if(DetailItems.get(position).number.equals("3")){
                    iv.setColorFilter(Color.parseColor("#fe5d10"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#fe5d10"));
                }else if(DetailItems.get(position).number.equals("4")){
                    iv.setColorFilter(Color.parseColor("#00a2d1"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#00a2d1"));
                }else if(DetailItems.get(position).number.equals("5")){
                    iv.setColorFilter(Color.parseColor("#8b50a4"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#8b50a4"));
                }else if(DetailItems.get(position).number.equals("6")){
                    iv.setColorFilter(Color.parseColor("#c55c1d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#c55c1d"));
                }else if(DetailItems.get(position).number.equals("7")){
                    iv.setColorFilter(Color.parseColor("#54640d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#54640d"));
                }else if(DetailItems.get(position).number.equals("8")){
                    iv.setColorFilter(Color.parseColor("#f14c82"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#f14c82"));
                }else if(DetailItems.get(position).number.equals("9")){
                    iv.setColorFilter(Color.parseColor("#aa9872"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#aa9872"));
                }

            }


        }else{
            tv3.setText("");
            iv.setImageResource(R.drawable.ic_run);
            tv.setText(DetailItems.get(position).name+" 하차");
        }

        return convertView;
    }
}
