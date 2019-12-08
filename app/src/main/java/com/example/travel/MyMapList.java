package com.example.travel;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
public class MyMapList extends Fragment {
    Button btn_main;
    ListView maplist;
    TextView txt_title,txt_date;
    private ArrayList<MyMapItem> mapitems = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.activity_maplist,null);

        Toolbar toolbar = (Toolbar)view.findViewById(R.id.toolbar); //툴바설정
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("My Travel");



        txt_date = view.findViewById(R.id.txt_date);
        txt_title = view.findViewById(R.id.txt_title);
        txt_date.setText(getString(R.string.maplistdate));
        txt_title.setText(getString(R.string.maplisttitle));
        btn_main = (Button) view.findViewById(R.id.btn_main);
        maplist = (ListView) view.findViewById(R.id.maplist);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), travel.class);
                startActivity(intent);
            }
        });
        try {
            mapitems.clear();
            SQLiteDatabase mapnamedb = getActivity().openOrCreateDatabase("mapname.db", MODE_PRIVATE, null);
            Cursor cursor1 = mapnamedb.rawQuery("SELECT * FROM mapnametb", null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    int mapnum = cursor1.getInt(0);
                    String mapname = cursor1.getString(1);
                    String mapdate = cursor1.getString(2);
                    mapitems.add(new MyMapItem(mapnum, mapname, mapdate));
                }
            }
            MyMapItemAdapter adapter = new MyMapItemAdapter(getActivity().getApplicationContext(), mapitems, R.layout.mapitem);
            maplist.setAdapter(adapter);
            maplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), TravelResult.class);
                        int mapnum = mapitems.get(position).mapnum;
                        intent.putExtra("mapnum", mapnum);
                        startActivity(intent);
                }
            });
        }catch (Exception e){

        }


            maplist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final CharSequence[] items = {"이름수정", "삭제", "취소"};//0, 1, 2
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            getContext());


                    alertDialogBuilder.setItems(items,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                    if (id == 0) {
                                        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());

                                        ad.setTitle("바꿀 이름을 입력해주세요");
                                        ad.setMessage("");

                                        final EditText et = new EditText(getContext());
                                        ad.setView(et);
                                        ad.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String value = et.getText().toString();
                                                try {
                                                    final SQLiteDatabase mapdb = getActivity().openOrCreateDatabase("mapname.db", MODE_PRIVATE, null);
                                                    String sql = "UPDATE mapnametb SET map_title = '" + value + "' WHERE number =" + mapitems.get(position).mapnum + ";";
                                                    mapdb.execSQL(sql);
                                                    mapitems.get(position).mapname = value;
                                                    MyMapItemAdapter adapter = new MyMapItemAdapter(getActivity().getApplicationContext(), mapitems, R.layout.mapitem);
                                                    maplist.setAdapter(adapter);
                                                    maplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                            Intent intent = new Intent(getActivity(), TravelResult.class);
                                                            int mapnum = mapitems.get(position).mapnum;
                                                            intent.putExtra("mapnum", mapnum);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }catch (Exception e){

                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        ad.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        ad.show();
                                    }else if(id == 1){
                                        try {
                                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                                            builder.setTitle("알림창");
                                            builder.setMessage("경로를 정말 지우시겠습니까?");
                                            builder.setPositiveButton("아니요",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                        }
                                                    });
                                            builder.setNegativeButton("예",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            String sql;

                                                            final SQLiteDatabase mapnamedb = getActivity().openOrCreateDatabase("mapname.db", MODE_PRIVATE, null);
                                                            sql = "DELETE FROM mapnametb WHERE number =" + mapitems.get(position).mapnum + ";";
                                                            mapnamedb.execSQL(sql);

                                                            final SQLiteDatabase mapdb = getActivity().openOrCreateDatabase("map.db", MODE_PRIVATE, null);
                                                            sql = "DELETE FROM maptb WHERE map_number =" + mapitems.get(position).mapnum + ";";
                                                            mapdb.execSQL(sql);

                                                            final SQLiteDatabase mapmemodb = getActivity().openOrCreateDatabase("mapmemo.db", MODE_PRIVATE, null);
                                                            sql = "DELETE FROM mapmemotb WHERE mapnum =" + mapitems.get(position).mapnum + ";";
                                                            mapmemodb.execSQL(sql);

                                                            Toast.makeText(getContext(), "삭제 했습니다.", Toast.LENGTH_LONG).show();
                                                            mapitems.remove(position);
                                                            MyMapItemAdapter adapter = new MyMapItemAdapter(getActivity().getApplicationContext(), mapitems, R.layout.mapitem);
                                                            maplist.setAdapter(adapter);
                                                            maplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                    Intent intent = new Intent(getActivity(), TravelResult.class);
                                                                    int mapnum = mapitems.get(position).mapnum;
                                                                    intent.putExtra("mapnum", mapnum);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    });
                                            builder.show();
                                        }catch (Exception e){

                                        }
                                    }else {

                                    }
                                    dialog.dismiss();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                    return true;
                }
            });






        return view;

    }

    @Override
    public void onResume() {
        try {
            mapitems.clear();
            SQLiteDatabase mapnamedb = getActivity().openOrCreateDatabase("mapname.db", MODE_PRIVATE, null);
            Cursor cursor1 = mapnamedb.rawQuery("SELECT * FROM mapnametb order by number desc", null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    int mapnum = cursor1.getInt(0);
                    String mapname = cursor1.getString(1);
                    String mapdate = cursor1.getString(2);
                    mapitems.add(new MyMapItem(mapnum, mapname, mapdate));
                }
            }
            MyMapItemAdapter adapter = new MyMapItemAdapter(getActivity().getApplicationContext(), mapitems, R.layout.mapitem);
            maplist.setAdapter(adapter);
            maplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), TravelResult.class);
                    int mapnum = mapitems.get(position).mapnum;
                    intent.putExtra("mapnum", mapnum);
                    startActivity(intent);
                }
            });
        }catch (Exception e){

        }
        super.onResume();
    }

}

