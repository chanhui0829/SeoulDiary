package com.example.travel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.MarkerOptions;


public class RecommendMap extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    String mapx,mapy,title,addr;
    double Lan,Lon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewmap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(ContextCompat.getDrawable(getBaseContext(), R.drawable.homelogo));

        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginEnd(50);
        toolbar.setTitle("Map View");
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


        final Intent intent = getIntent();
        mapx = intent.getStringExtra("mapx");
        mapy = intent.getStringExtra("mapy");
        title = intent.getStringExtra("title");
        addr = intent.getStringExtra("addr");

        Lan = Double.parseDouble(mapy);
        Lon = Double.parseDouble(mapx);

        MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onMapReady(final GoogleMap googlemap) {
        mMap = googlemap;

        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(new LatLng(Lan, Lon))
                .title(title)
                .snippet(addr)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favoritemark));;
        mMap.addMarker(makerOptions);
        LatLng startPoint = new LatLng(Lan,Lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,16));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
