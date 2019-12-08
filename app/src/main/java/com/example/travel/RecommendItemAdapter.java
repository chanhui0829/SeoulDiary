package com.example.travel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecommendItemAdapter extends BaseAdapter {
    Context context;
    Button btn_cos;
    LayoutInflater inflater;
    SQLiteDatabase db;
    Cursor cursor;
    String contentid2, mapx,mapy,title;
    ArrayList<String> content = new ArrayList<>();
    String a,b = "false" ;

    private ArrayList<RecommendItem> myTourItems = null;
    private int nListCnt = 0;



    public RecommendItemAdapter(Context context, ArrayList<RecommendItem> myTourItems, int layout){
        this.context = context;
        this.myTourItems = myTourItems;
        this.inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public RecommendItemAdapter(ArrayList<RecommendItem> myTourItems2) {
        myTourItems = myTourItems2;
        nListCnt=myTourItems.size();
    }

    @Override
    public int getCount() {
        return myTourItems.size();
    }

    @Override
    public Object getItem(int position) {
        return myTourItems.get(position).title;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
        {
            Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.recommenditem, parent, false);

        }


        btn_cos = convertView.findViewById(R.id.btn_cos);
        btn_cos.setFocusable(false);

        int i =0;
        btn_cos= convertView.findViewById(R.id.btn_cos);
        DBRecommend Helper = new DBRecommend(inflater.getContext());
        db = Helper.getWritableDatabase();
        String sql  = "select * from recommendcos";
        cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            do {
                contentid2 = cursor.getString(0);
                content.add(i,contentid2);
                if ((myTourItems.get(position).contentid).equals(content.get(i))) {
                    a="true";
                    break;
                }
                else{
                    a="false";
                }
                i++;
            } while (cursor.moveToNext());
        }else{
            a="false";
        }

        if(a.equals("true")){
            btn_cos.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.ic_favorite2));
        }

        if(a.equals("false")) {
            btn_cos.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.ic_favorite));
        }



        btn_cos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j =0;
                btn_cos= v.findViewById(R.id.btn_cos);
                DBRecommend Helper = new DBRecommend(inflater.getContext());
                db = Helper.getWritableDatabase();
                String sql  = "select * from recommendcos";
                cursor = db.rawQuery(sql, null);
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    do {
                        contentid2 = cursor.getString(0);
                        content.add(j,contentid2);
                        if ((myTourItems.get(position).contentid).equals(content.get(j))) {
                            a="true";
                            break;
                        }
                        else{
                            a="false";
                        }
                        j++;
                    } while (cursor.moveToNext());
                }else{
                    a="false";
                }
                if(a.equals("true")){
                    db.execSQL("delete from recommendcos where contentTypeId = '" + myTourItems.get(position).contentid + "';");
                    Toast.makeText(inflater.getContext(), inflater.getContext().getString(R.string.favorite2), Toast.LENGTH_SHORT).show();
                    btn_cos.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.ic_favorite));
                }
                if(a.equals("false")) {
                    db.execSQL("insert into recommendcos values ('" + myTourItems.get(position).contentid + "','"
                            + myTourItems.get(position).title + "','" + myTourItems.get(position).mapx + "','"
                            + myTourItems.get(position).mapy + "','"+myTourItems.get(position).addr+"');");
                    Toast.makeText(inflater.getContext(), inflater.getContext().getString(R.string.favorite1), Toast.LENGTH_SHORT).show();
                    btn_cos.setBackground(ContextCompat.getDrawable(inflater.getContext(), R.drawable.ic_favorite2));
                }
                db.close();
            }


        });


        /*
        btn_cos.setBackground(inflater.getContext().getResources().
                            getDrawable(R.drawable.ic_01d));
                    db.execSQL("delete from recommendcos where contentTypeId = '" + myTourItems.get(position).contentid +"';");

                    Toast.makeText(inflater.getContext(), "관심상품에 제거 했습니다.", Toast.LENGTH_LONG).show();

         */

        TextView title2 = convertView.findViewById(R.id.txt_title);
        myTourItems.get(position).title=Html.fromHtml(myTourItems.get(position).title).toString();
        title2.setText(myTourItems.get(position).title);


        TextView addr = convertView.findViewById(R.id.txt_addr);
        myTourItems.get(position).addr=Html.fromHtml(myTourItems.get(position).addr).toString();
        addr.setText(myTourItems.get(position).addr);

        TextView tel = convertView.findViewById(R.id.txt_tel);
        String tel2 = myTourItems.get(position).tel;
        if (tel2.equals(" ")){
            tel.setText(" ");
        }
        else {
            tel2 = Html.fromHtml(tel2).toString();
            tel.setText("tel)"+tel2);
        }




        ImageView img_tour = convertView.findViewById(R.id.img_tour);
        String firstimage = myTourItems.get(position).firstimage;
        if (firstimage.equals("R.drawable.image")){
            img_tour.setImageResource(R.drawable.image);
        }
        else {
            Glide.with(convertView).load(firstimage).override(110,110).into(img_tour);
        }




        return convertView;
    }


}
