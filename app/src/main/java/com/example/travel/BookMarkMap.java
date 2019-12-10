package com.example.travel;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class BookMarkMap extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    String mapx,mapy,title,addr,contentid;
    Cursor cursor;
    SQLiteDatabase db;
    DBRecommend Helper;
    MarkerOptions makerOptions;
    Double lan,lon;
    int i =0;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bookmarkmap);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar); //툴바설정
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("My favorite");
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



        Helper = new DBRecommend(this);
        db = Helper.getWritableDatabase();


        MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void onMapReady(final GoogleMap googlemap) {

        mMap =googlemap;
        Helper = new DBRecommend(this);
        db = Helper.getWritableDatabase();
        String sql  = "select * from recommendcos";



        cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.getCount() != 0) {

            cursor.moveToFirst();

            do {
                contentid = cursor.getString(0);
                title = cursor.getString(1);
                mapx = cursor.getString(2);
                mapy = cursor.getString(3);
                addr = cursor.getString(4);

                lan = Double.parseDouble(mapy);
                lon = Double.parseDouble(mapx);

                makerOptions= new MarkerOptions();

                makerOptions.position(new LatLng(lan, lon))
                            .title(title)
                            .snippet(contentid)

                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favoritemark));
                // 2. 마커 생성 (마커를 나타냄)
                mMap.addMarker(makerOptions);
                i++;

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    public boolean onMarkerClick(Marker marker) {
                        String text =  marker.getSnippet();
                        Intent intent = new Intent(getApplicationContext(), RecommendTourDetail.class);
                        intent.putExtra("contentid",text);
                        startActivity(intent);

                        return false;
                    }
                });

            } while (cursor.moveToNext());



        }

        LatLng startPoint = new LatLng(37.547030,126.984018);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,10));

    }


}
