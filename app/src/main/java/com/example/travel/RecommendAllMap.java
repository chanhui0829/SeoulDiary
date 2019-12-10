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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecommendAllMap  extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    GpsTracker gpsTracker;

    double mylatitude,mylongitude,lat,lon;
    String result,language,mapx,mapy,contentid,title;
    String keynum = "servicekey";
    int page = 1;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommendallmap);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
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

        if(Home.languagetype.equals("0")){
            language = "KorService";        }
        else if(Home.languagetype.equals("1")){
            language = "EngService";
        }

        gpsTracker = new GpsTracker(getApplicationContext());

        mylatitude = gpsTracker.getLatitude();
        mylongitude = gpsTracker.getLongitude();

        getJSON();

        MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    public void getJSON() {

        result=null;

        new Thread() {

            public void run() {
                try {

                    String url2 = "http://api.visitkorea.or.kr/openapi/service/rest/"+language+"/areaBasedList?" +
                            "ServiceKey="+keynum+"&MobileOS=ETC&MobileApp=appName&areaCode=1&pageNo="+page+"&numOfRows=10&_type=json";
                    //Toast.makeText(getApplicationContext(),Integer.toString(page),Toast.LENGTH_SHORT).show();
                    URL url = new URL(url2);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    if (conn != null) {
                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(30000);
                        conn.setRequestMethod("GET");

                        int resCode = conn.getResponseCode();
                        if (resCode == HttpURLConnection.HTTP_OK) {
                            InputStream is = conn.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            StringBuilder strBuilder = new StringBuilder();
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                strBuilder.append(line + "\n");
                            }
                            result = strBuilder.toString();

                            br.close();
                            conn.disconnect();
                        }
                    }
                }catch (Exception ex){
                }
            }
        }.start();


        while(result==null) {
            try {
                Thread.sleep(0);
            }catch (InterruptedException ie){
            }
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsonParser(result);
            }
        });
    }



    public boolean jsonParser(String jsonString) {

        if (jsonString == null) return false;

        try {

            JSONObject json = new JSONObject(jsonString);
            JSONObject requestjson = json.getJSONObject("response");
            JSONObject bodyjson = requestjson.getJSONObject("body");

            JSONObject items = bodyjson.getJSONObject("items");


            String totalCount = bodyjson.getString("totalCount");
            final String pageNo = bodyjson.getString("pageNo");

            int numOfRows = bodyjson.getInt("numOfRows");
            final int tc = Integer.parseInt(totalCount);
            int pn = Integer.parseInt(pageNo);


            if ((tc% pn == 1 && page == pn) ||((tc == 1)&&(pn==1))) {
                JSONObject item = items.getJSONObject("item");
                mapx = item.getString("mapx");
                mapy = item.getString("mapy");
                contentid = item.getString("contentid");

                MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            }else{

                JSONArray jArr = items.getJSONArray("item");

                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject tourInfo = jArr.getJSONObject(i);

                    mapx = tourInfo.getString("mapx");
                    mapy = tourInfo.getString("mapy");
                    contentid = tourInfo.getString("contentid");
                    lat = Double.parseDouble(mapy);
                    lon = Double.parseDouble(mapx);


                    MapFragment mapFragment = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }


                if(page<=tc/numOfRows){
                    page++;
                    getJSON();
                }


            }


            }catch (JSONException e) {

        }

        return false;
    }

    public void onMapReady(final GoogleMap googlemap) {

        mMap = googlemap;
        MarkerOptions makerOptions = new MarkerOptions();
        makerOptions
                .position(new LatLng(mylatitude, mylongitude))
                .title("현재위치")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.favoritemark));;
        mMap.addMarker(makerOptions);
        LatLng startPoint = new LatLng(mylatitude,mylongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,12));


        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(new LatLng(lat,lon))
                .title(contentid);
        mMap.addMarker(markerOptions2);
    }

}
