package com.example.travel;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import static com.example.travel.ListViewSize.BitmapToString;
import static com.example.travel.ListViewSize.StringToBitmap;

public class TravelResult
        extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    GoogleMap mMap;
    PolylineOptions polylineOptions;
    private static int PICK_IMAGE_REQUEST = 1;
    int count = 0;
    int mapnum;
    double str_latitude, str_longtitude;
    String inserbit;

    ListView re_memolistd;
    Button btn_memolist, btn_mapview,tribtn_yes, tribtn_no,btn_insert, btn_del, btn_cencel;
    TextView tr_content,tr_date,tr_tvcate,tr_title,tri_title, tri_content;
    ImageView tr_iv,tri_iv;
    LinearLayout memo_meaning, noclickVi,tri_layout;
    Marker del_marker;
    RadioGroup tri_select;




    ArrayList<MyMarkerItem> myMarkerItems = new ArrayList<>();
    ArrayList<Double> yy=new ArrayList<>();
    ArrayList<Double> xx=new ArrayList<>();


    LinearLayout re_memolist;

    @Override
    public void onMapReady(final GoogleMap map) {
        Intent CheckMemo_Intent = getIntent();
        mapnum=CheckMemo_Intent.getExtras().getInt("mapnum");


        mMap = map;
        mMap.setOnMarkerClickListener(this);
        try {
            SQLiteDatabase mapdb = this.openOrCreateDatabase("map.db", MODE_PRIVATE, null);
            Cursor cursor1 = mapdb.rawQuery("SELECT * FROM maptb WHERE map_number = "+mapnum, null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    double x = cursor1.getDouble(2);
                    double y = cursor1.getDouble(3);
                    yy.add(y);
                    xx.add(x);
                }
            }


            cursor1.close();
            mapdb.close();
        }catch (Exception e){
        } try {
            SQLiteDatabase mapmemodb = this.openOrCreateDatabase("mapmemo.db", MODE_PRIVATE, null);
            Cursor cursor1 = mapmemodb.rawQuery("SELECT * FROM mapmemotb WHERE mapnum = "+mapnum, null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    int num = cursor1.getInt(1);
                    String title = cursor1.getString(2);
                    String memo = cursor1.getString(3);
                    String check = cursor1.getString(4);
                    String date = cursor1.getString(5);
                    String bitm = cursor1.getString(6);
                    Double x = cursor1.getDouble(7);
                    Double y = cursor1.getDouble(8);

                    LatLng point = new LatLng(y,x);
                    MarkerOptions makerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw2;
                    bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.favoritemark);

                    Bitmap b=bitmapdraw2.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    makerOptions
                            .position(point)
                            .title(title);
                    mMap.addMarker(makerOptions);
                    myMarkerItems.add(new MyMarkerItem(num,title,memo,date,check,x,y,bitm));
                }
            }
            MyMarkerAdapter adapter = new MyMarkerAdapter(getApplicationContext(),myMarkerItems,R.layout.mapmarkitem);
            re_memolistd.setAdapter(adapter);
            re_memolistd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LatLng point = new LatLng(myMarkerItems.get(position).y, myMarkerItems.get(position).x);
                    re_memolist.setVisibility(View.GONE);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
                }
            });

            cursor1.close();
            mapmemodb.close();
        }catch (Exception e){
        }


        ArrayList<LatLng> arrayPoints= new ArrayList<>();

        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        for(int i=0;i<xx.size();i++){
            LatLng point = new LatLng(yy.get(i),xx.get(i));


            arrayPoints.add(point);
            if(i==0){
                MarkerOptions makerOptions = new MarkerOptions();

                BitmapDrawable bitmapdraw;
                if(Home.languagetype.equals("0")) {
                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_start);
                }
                else{
                    bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_start_en);
                }

                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                makerOptions
                        .position(point)
                        .title(getString(R.string.t_markstart));
                mMap.addMarker(makerOptions);
            }else if(i==xx.size()-1){
                MarkerOptions makerOptions = new MarkerOptions();






                BitmapDrawable bitmapdraw2;
                if(Home.languagetype.equals("0")) {
                    bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_end);
                }
                else{
                    bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_end_en);
                }

                Bitmap b=bitmapdraw2.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));


                makerOptions
                        .position(point)
                        .title(getString(R.string.t_markend));
                mMap.addMarker(makerOptions);
            }

        }
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
        LatLng mypoint = new LatLng(yy.get(0),xx.get(0));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mypoint,15));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelresult);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("My Travel");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View view = toolbar.getChildAt(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homelogo = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(homelogo);
            }
        });

        final SQLiteDatabase mapdb = this.openOrCreateDatabase("mapmemo.db", MODE_PRIVATE, null);

        tri_select = findViewById(R.id.tri_select);
        tri_title = findViewById(R.id.tri_title);
        tri_content = findViewById(R.id.tri_content);
        tri_iv = findViewById(R.id.tri_iv);
        tri_layout = findViewById(R.id.tri_layout);
        tribtn_yes = findViewById(R.id.tribtn_yes);
        tribtn_no = findViewById(R.id.tribtn_no);

        tr_iv = findViewById(R.id.tr_iv);
        btn_insert = findViewById(R.id.btn_insert);
        btn_del = findViewById(R.id.btn_del);
        btn_cencel = findViewById(R.id.btn_cencel);
        tr_content = findViewById(R.id.tr_content);
        tr_date = findViewById(R.id.tr_date);
        tr_tvcate = findViewById(R.id.tr_tvcate);
        tr_title = findViewById(R.id.tr_title);
        memo_meaning = findViewById(R.id.memo_meaning);
        noclickVi = findViewById(R.id.noclickVi);

        tribtn_yes.setOnClickListener(new View.OnClickListener() {                  //수정된거 저장하기 버튼
            @Override
            public void onClick(View v) {
                RadioButton rd = (RadioButton) findViewById(tri_select.getCheckedRadioButtonId());
                String check = rd.getText().toString();
                String title = tri_title.getText().toString();
                String content = tri_content.getText().toString();
                if(tri_title.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_titlehint,Toast.LENGTH_SHORT).show();
                }else if(tri_content.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_contenthint,Toast.LENGTH_SHORT).show();
                }else if(tri_title.getText().toString().equals("현재위치")||tri_title.getText().toString().equals("Mylocation")||
                        tri_title.getText().toString().equals("시작")||tri_title.getText().toString().equals("departure")||
                        tri_title.getText().toString().equals("끝")||tri_title.getText().toString().equals("arrive")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_notitle,Toast.LENGTH_SHORT).show();
                }else {
                    String sql = "UPDATE mapmemotb SET title = '" + title + "', memo = '" + content + "', bitmap ='" + inserbit + "', priority ='" + check + "' WHERE mapnum =" + mapnum + " AND y =" + str_latitude + " AND x =" + str_longtitude + ";";
                    mapdb.execSQL(sql);
                    tr_iv.setImageBitmap(StringToBitmap(inserbit));
                    tr_title.setText(title);
                    tr_content.setText(content);
                    tr_tvcate.setText(check);
                    del_marker.remove();
                    LatLng point = new LatLng(str_latitude, str_longtitude);
                    MarkerOptions makerOptions = new MarkerOptions();
                    BitmapDrawable bitmapdraw2;
                    bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.favoritemark);
                    Bitmap b=bitmapdraw2.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                    makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    makerOptions
                            .position(point)
                            .title(title);
                    mMap.addMarker(makerOptions);
                    memo_meaning.setVisibility(View.VISIBLE);
                    tri_layout.setVisibility(View.GONE);
                }
            }
        });

        tribtn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo_meaning.setVisibility(View.VISIBLE);
                tri_layout.setVisibility(View.GONE);
            }
        });

        tri_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memo_meaning.setVisibility(View.GONE);
                tri_layout.setVisibility(View.VISIBLE);
            }
        });

        noclickVi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tri_layout.getVisibility()==View.VISIBLE){
                    memo_meaning.setVisibility(View.VISIBLE);
                    tri_layout.setVisibility(View.GONE);

                }else {
                    noclickVi.setVisibility(View.GONE);
                    memo_meaning.setVisibility(View.GONE);

                }
            }
        });
        btn_cencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noclickVi.setVisibility(View.GONE);
                memo_meaning.setVisibility(View.GONE);
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        btn_del.setOnClickListener(new View.OnClickListener() {//마커 삭제
            @Override
            public void onClick(View v) {

                builder.setTitle(getString(R.string.tri_dialtitle));
                builder.setMessage(getString(R.string.tri_dialcontent));
                builder.setPositiveButton(R.string.tri_dialyes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), R.string.tm_dialdelyse, Toast.LENGTH_LONG).show();
                                String sql = "DELETE FROM mapmemotb WHERE mapnum =" + mapnum + " AND y =" + str_latitude + " AND x =" + str_longtitude + ";";
                                mapdb.execSQL(sql);

                                memo_meaning.setVisibility(View.GONE);
                                noclickVi.setVisibility(View.GONE);
                                del_marker.remove();

                                for(int i=0;i<myMarkerItems.size();i++){
                                    if(myMarkerItems.get(i).x==str_longtitude&&myMarkerItems.get(i).y==str_latitude){
                                        myMarkerItems.remove(i);
                                    }
                                }

                                MyMarkerAdapter adapter = new MyMarkerAdapter(getApplicationContext(),myMarkerItems,R.layout.mapmarkitem);
                                re_memolistd.setAdapter(adapter);
                                re_memolistd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        LatLng point = new LatLng(myMarkerItems.get(position).y, myMarkerItems.get(position).x);
                                        re_memolist.setVisibility(View.GONE);
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
                                    }
                                });

                            }
                        });
                builder.setNegativeButton(R.string.tri_dialno,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }
        });


        re_memolistd = findViewById(R.id.re_memolistd);
        btn_memolist = findViewById(R.id.btn_memolist);
        btn_mapview = findViewById(R.id.btn_mapview);
        Intent intent = getIntent();

        mapnum = intent.getIntExtra("mapnum",1);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.result_map);
        mapFragment.getMapAsync(this);

        re_memolist = (LinearLayout) findViewById(R.id.re_memolist);
        btn_memolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                re_memolist.setVisibility(View.VISIBLE);
                count = 1 ;
            }
        });

        btn_mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                re_memolist.setVisibility(View.GONE);
                count= 0;
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {

                Uri uri = data.getData();
                Bitmap scaled;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                inserbit = BitmapToString(scaled);

                tri_iv.setImageBitmap(scaled);

            } else {

            }

        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }



    @Override
    public void onBackPressed(){
        if(tri_layout.getVisibility()==View.VISIBLE){
            memo_meaning.setVisibility(View.VISIBLE);
            tri_layout.setVisibility(View.GONE);

        }else if(memo_meaning.getVisibility()==View.VISIBLE&&tri_layout.getVisibility()!=View.VISIBLE){
            noclickVi.setVisibility(View.GONE);
            memo_meaning.setVisibility(View.GONE);
        }else if(re_memolist.getVisibility()==View.VISIBLE){
            re_memolist.setVisibility(View.GONE);
        }else{
            finish();
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals("시작")||marker.getTitle().equals("departure")||marker.getTitle().equals("끝")||marker.getTitle().equals("arrive")) {

        }else {
            final SQLiteDatabase mapdb = this.openOrCreateDatabase("mapmemo.db", MODE_PRIVATE, null);
            str_latitude = marker.getPosition().latitude;
            str_longtitude = marker.getPosition().longitude;
            del_marker = marker;

            String sql = "SELECT * FROM mapmemotb WHERE mapnum =" + mapnum + " AND y =" + str_latitude + " AND x =" + str_longtitude + ";";
            Cursor cursor1 = mapdb.rawQuery(sql, null);
            if (cursor1.getCount() > 0) {
                while (cursor1.moveToNext()) {
                    int num = cursor1.getInt(1);
                    String title = cursor1.getString(2);
                    String memo = cursor1.getString(3);
                    String check = cursor1.getString(4);
                    String date = cursor1.getString(5);
                    String bitm = cursor1.getString(6);

                    tri_title.setText(title);
                    tri_content.setText(memo);
                    inserbit = bitm;
                    if(check.equals("관광지")||check.equals("Tourist spot")) {
                        tri_select.check(R.id.tri_s1);
                    }else if(check.equals("맛집")||check.equals("Restaurant")) {
                        tri_select.check(R.id.tri_s2);
                    }else if(check.equals("인물")||check.equals("Person")) {
                        tri_select.check(R.id.tri_s3);
                    }else {
                        tri_select.check(R.id.tri_s4);
                    }

                    tr_title.setText(title);
                    tr_content.setText(memo);
                    tr_tvcate.setText(check);
                    tr_date.setText(date);

                    Bitmap bb = StringToBitmap(bitm);
                    if (bitm.equals("")) {
                        tr_iv.setImageResource(R.drawable.btn_save);
                        tri_iv.setImageResource(R.drawable.btn_save);
                    } else {
                        tr_iv.setImageBitmap(bb);
                        tri_iv.setImageBitmap(bb);
                    }
                }
            }
            memo_meaning.setVisibility(View.VISIBLE);
            noclickVi.setVisibility(View.VISIBLE);

            cursor1.close();
            mapdb.close();
        }
        return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if(count == 1 ){
                    re_memolist.setVisibility(View.GONE);
                }
                else {
                    finish();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}