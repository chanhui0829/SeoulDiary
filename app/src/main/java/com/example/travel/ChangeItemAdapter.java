package com.example.travel;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeItemAdapter extends BaseAdapter {
    Context context;
    ArrayList<ChangeItem> changeItems;
    int layout;
    LayoutInflater inflater;

    public ChangeItemAdapter(Context context, ArrayList<ChangeItem> changeItems, int layout){
        this.context = context;
        this.changeItems = changeItems;
        this.layout = layout;

        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return changeItems.size();
    }

    @Override
    public Object getItem(int position) {

        return changeItems.get(position).type;
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

        ImageView iv_icon = convertView.findViewById(R.id.iv_icon);
        TextView tv2 = convertView.findViewById(R.id.tv_startNumber);
        if(changeItems.get(position).number.isEmpty()){
            tv2.setText("");
        }else if(changeItems.get(position).type == 2 && !(changeItems.get(position).number.isEmpty())){
            tv2.setText(changeItems.get(position).number);
            iv_icon.setImageResource(R.drawable.ic_bus);
        }else if(changeItems.get(position).type == 1 && !changeItems.get(position).number.isEmpty()){
            iv_icon.setImageResource(R.drawable.ic_train);
            if(changeItems.get(position).number.equals("100")){
                tv2.setText("분당선 ");
                iv_icon.setColorFilter(Color.parseColor("#ff8c00"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ff8c00"));
            }else if(changeItems.get(position).number.equals("101")) {
                tv2.setText("공항철도 ");
                iv_icon.setColorFilter(Color.parseColor("#3681b7"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#3681b7"));
            }else if(changeItems.get(position).number.equals("104")) {
                tv2.setText("경의중앙선 ");
                iv_icon.setColorFilter(Color.parseColor("#73c7a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#73c7a6"));
            }else if(changeItems.get(position).number.equals("107")) {
                tv2.setText("에버라인 ");
                iv_icon.setColorFilter(Color.parseColor("#4ea346"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#4ea346"));
            }else if(changeItems.get(position).number.equals("108")) {
                tv2.setText("경춘선 ");
                iv_icon.setColorFilter(Color.parseColor("#32c6a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#32c6a6"));
            }else if(changeItems.get(position).number.equals("102")) {
                tv2.setText("자기부상철도 ");
                iv_icon.setColorFilter(Color.parseColor("#ffcd12"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ffcd12"));
            }else if(changeItems.get(position).number.equals("109")) {
                tv2.setText("신분당선 ");
                iv_icon.setColorFilter(Color.parseColor("#c82127"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#c82127"));
            }else if(changeItems.get(position).number.equals("110")) {
                tv2.setText("의정부경전철 ");
                iv_icon.setColorFilter(Color.parseColor("#fda600"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#fda600"));
            }else if(changeItems.get(position).number.equals("111")) {
                tv2.setText("수인선 ");
                iv_icon.setColorFilter(Color.parseColor("#ff8c00"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ff8c00"));
            }else if(changeItems.get(position).number.equals("112")) {
                tv2.setText("경강선 ");
                iv_icon.setColorFilter(Color.parseColor("#0054a6"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#0054a6"));
            }else if(changeItems.get(position).number.equals("113")) {
                tv2.setText("우이신설선 ");
                iv_icon.setColorFilter(Color.parseColor("#B0CE18"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#B0CE18"));
            }else if(changeItems.get(position).number.equals("114")) {
                tv2.setText("서해선 ");
                iv_icon.setColorFilter(Color.parseColor("#8FC31E"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#8FC31E"));
            }else if(changeItems.get(position).number.equals("21")) {
                tv2.setText("인천 1호선 ");
                iv_icon.setColorFilter(Color.parseColor("#8cadcb"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#8cadcb"));
            }else if(changeItems.get(position).number.equals("22")) {
                tv2.setText("인천 2호선 ");
                iv_icon.setColorFilter(Color.parseColor("#ed8000"), PorterDuff.Mode.SRC_IN);
                tv2.setTextColor(Color.parseColor("#ed8000"));
            }else {
                tv2.setText(changeItems.get(position).number + "호선 ");
                if (changeItems.get(position).number.equals("1")) {
                    iv_icon.setColorFilter(Color.parseColor("#0d3692"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#0d3692"));
                } else if (changeItems.get(position).number.equals("2")) {
                    iv_icon.setColorFilter(Color.parseColor("#33a23d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#33a23d"));
                } else if (changeItems.get(position).number.equals("3")) {
                    iv_icon.setColorFilter(Color.parseColor("#fe5d10"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#fe5d10"));
                } else if (changeItems.get(position).number.equals("4")) {
                    iv_icon.setColorFilter(Color.parseColor("#00a2d1"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#00a2d1"));
                } else if (changeItems.get(position).number.equals("5")) {
                    iv_icon.setColorFilter(Color.parseColor("#8b50a4"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#8b50a4"));
                } else if (changeItems.get(position).number.equals("6")) {
                    iv_icon.setColorFilter(Color.parseColor("#c55c1d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#c55c1d"));
                } else if (changeItems.get(position).number.equals("7")) {
                    iv_icon.setColorFilter(Color.parseColor("#54640d"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#54640d"));
                } else if (changeItems.get(position).number.equals("8")) {
                    iv_icon.setColorFilter(Color.parseColor("#f14c82"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#f14c82"));
                } else if (changeItems.get(position).number.equals("9")) {
                    iv_icon.setColorFilter(Color.parseColor("#aa9872"), PorterDuff.Mode.SRC_IN);
                    tv2.setTextColor(Color.parseColor("#aa9872"));
                }
            }
            }

        TextView tv = convertView.findViewById(R.id.tv_startName);
        tv.setText(changeItems.get(position).name);



        TextView tv3 = convertView.findViewById(R.id.tv_startReplace);

        if(changeItems.get(position).replace!=""){
            tv3.setVisibility(View.VISIBLE);
        }
        tv3.setText(""+changeItems.get(position).replace);

        return convertView;
    }
}
