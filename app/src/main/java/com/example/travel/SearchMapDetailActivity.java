package com.example.travel;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static android.graphics.Color.*;
import static android.view.View.GONE;
import static com.example.travel.Home.languagetype;


public class SearchMapDetailActivity extends AppCompatActivity
        implements OnMapReadyCallback {
    private static final String TAG_PARSE = "TAG_PARSE";
    private  String busRouteId;
    String result;
    LinearLayout ll_busTime;
    LinearLayout ll_busInfo;
    Button btn_refresh;
    private ODsayService odsayService;
    int toggle;
    boolean isPageOpen = true;

    float upX,downX;
    Animation translateBottom,translateTop;

    LinearLayout detailLayout;
    View list_recall;

    private PolylineOptions polylineOptions;
    private PolylineOptions polylineOptions_walk;
    private PolylineOptions polylineOptions_walk2;
    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> arrayPoints_walk;
    private ArrayList<LatLng> arrayPoints_walk2;
    private ArrayList<LatLng> markerPoints;
    Double endX, endY;


    private ArrayList<DetailItem> detailItems = new ArrayList<>();//

    GoogleMap mMap;
    String mapObj;
    JSONObject SubJsonObject;
    JSONObject jsonObject;
    ListView detailList;


    int onoff=1;

    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;
        if (Build.VERSION.SDK_INT >= 23 &&//API가 23이상이고
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }else {
            mMap.setMyLocationEnabled(true);
        }

        try{
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(onoff==1) {
                        onoff=0;
                        detailLayout.startAnimation(translateTop);
                    }
                }
            });
        } catch (Exception e){
        }

        odsayService.requestLoadLane(mapObj, onResultCallbackListener);

    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {

        @Override
        public void onSuccess(ODsayData oDsayData, API api) {

            toggle = 0;
            markerPoints = new ArrayList<LatLng>();
            polylineOptions = new PolylineOptions();
            polylineOptions.color(parseColor("#4CFF0000"));
            polylineOptions.width(20).geodesic(true);
            polylineOptions_walk = new PolylineOptions();
            polylineOptions_walk.color(parseColor("#4CFF0000"));
            polylineOptions_walk.width(20).geodesic(true).pattern(Arrays.<PatternItem>asList(
                    new Dot(), new Gap(20), new Dash(30), new Gap(20)));
            polylineOptions_walk2 = new PolylineOptions();
            polylineOptions_walk2.color(parseColor("#4CFF0000"));
            polylineOptions_walk2.width(20).geodesic(true).pattern(Arrays.<PatternItem>asList(
                    new Dot(), new Gap(20), new Dash(30), new Gap(20)));
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            arrayPoints = new ArrayList<LatLng>();
            arrayPoints_walk = new ArrayList<LatLng>();
            arrayPoints_walk2 = new ArrayList<LatLng>();
            try {
                JSONObject jsonObject = oDsayData.getJson();
                String result = jsonObject.getString("result");
                JSONObject firstObject = new JSONObject(result);
                String lane = firstObject.getString("lane");
                JSONArray laneArray = new JSONArray(lane);
                arrayPoints_walk.add(new LatLng(Double.parseDouble(start[1]), Double.parseDouble(start[0])));

                MarkerOptions smakerOptions = new MarkerOptions();
                BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_start);
                if(languagetype.equalsIgnoreCase("1")){
                    bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_start_en);
                }
                Bitmap bi=bitmapdraw2.getBitmap();
                Bitmap smallMarker2 = Bitmap.createScaledBitmap(bi, 100, 100, false);
                smakerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                smakerOptions
                        .position(new LatLng(Double.parseDouble(start[1]), Double.parseDouble(start[0])));
                mMap.addMarker(smakerOptions);

                for(int i = 0; i<laneArray.length();i++) {
                    JSONObject sectionObject = laneArray.getJSONObject(i);
                    String section = sectionObject.getString("section");
                    JSONArray graphPosObject = new JSONArray(section);
                    JSONObject graphPos = graphPosObject.getJSONObject(0);
                    String xytry = graphPos.getString("graphPos");
                    JSONArray xyArray = new JSONArray(xytry);
                    for (int j = 0; j < xyArray.length(); j++) {
                        JSONObject xyObject = xyArray.getJSONObject(j);
                        Double x = Double.parseDouble(xyObject.getString("x"));
                        Double y = Double.parseDouble(xyObject.getString("y"));
                        arrayPoints.add(new LatLng(y, x));

                        if(i==0 && j==0){
                            arrayPoints_walk.add(new LatLng(y, x));
                            polylineOptions_walk.addAll(arrayPoints_walk);
                            mMap.addPolyline(polylineOptions_walk);
                        }else if(j==xyArray.length()-1 && i == laneArray.length()-1){
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(y-0.05, x)));
                            arrayPoints_walk2.add(new LatLng(y, x));
                        }
                    }
                }

                MarkerOptions makerOptions = new MarkerOptions();
                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_end);
                if(languagetype.equalsIgnoreCase("1")){
                    bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_end_en);
                }
                Bitmap b=bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);
                makerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                makerOptions
                        .position(new LatLng(Double.parseDouble(end[1]), Double.parseDouble(end[0])));
                mMap.addMarker(makerOptions);
                polylineOptions.addAll(arrayPoints);
                mMap.addPolyline(polylineOptions);

                arrayPoints_walk2.add(new LatLng(Double.parseDouble(end[1]), Double.parseDouble(end[0])));
                polylineOptions_walk2.addAll(arrayPoints_walk2);
                mMap.addPolyline(polylineOptions_walk2);

                CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                mMap.animateCamera(zoom);

                for(int i =0;i<detailItems.size();i++){
                    LatLng click = new LatLng(detailItems.get(i).y, detailItems.get(i).x);

                    if(i%2==1){
                        MarkerOptions getinmarkeroption = new MarkerOptions();
                        BitmapDrawable inbitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_getout);
                        if(languagetype.equalsIgnoreCase("1")){
                            inbitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_getout_en);
                        }
                        Bitmap bsi=inbitmapdraw2.getBitmap();
                        Bitmap smallMarker3 = Bitmap.createScaledBitmap(bsi, 100, 100, false);
                        getinmarkeroption.icon(BitmapDescriptorFactory.fromBitmap(smallMarker3));
                        getinmarkeroption
                                .position(click);
                        mMap.addMarker(getinmarkeroption);
                    }else{
                        MarkerOptions getinmarkeroption = new MarkerOptions();
                        BitmapDrawable inbitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_getin);
                        if(languagetype.equalsIgnoreCase("1")){
                            inbitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.icon_getin_en);
                        }
                        Bitmap bsi=inbitmapdraw2.getBitmap();
                        Bitmap smallMarker3 = Bitmap.createScaledBitmap(bsi, 100, 100, false);
                        getinmarkeroption.icon(BitmapDescriptorFactory.fromBitmap(smallMarker3));
                        getinmarkeroption
                                .position(click);
                        mMap.addMarker(getinmarkeroption);
                    }


                }

            }catch(JSONException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void onError(int i, String s, API api) {

        }
    };

    String start[];
    String end[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(ContextCompat.getDrawable(getBaseContext(), R.drawable.homelogo));

        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginEnd(50);
        toolbar.setTitle("Public Transport");
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


        odsayService = ODsayService.init(getApplicationContext(),getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        detailList=findViewById(R.id.detailList);
        detailLayout=findViewById(R.id.detailLayout);
        list_recall=findViewById(R.id.list_recall);
        ll_busTime=findViewById(R.id.ll_busTime);
        ll_busInfo = findViewById(R.id.ll_busInfo);
        btn_refresh=findViewById(R.id.btn_refresh);

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRealBusTime(busRouteId);
            }
        });

        Intent intent = getIntent();
        start = intent.getStringExtra("startXY").split(" ");
        end = intent.getStringExtra("endXY").split(" ");

        mapObj ="0:0@"+ intent.getStringExtra("mapObj");

        try {
            SubJsonObject = new JSONObject(intent.getStringExtra("passStopList"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        try {
            TrafficInfoResult sub = new TrafficInfoResult(SubJsonObject);
            JSONArray subPathArray = new JSONArray(sub.getSubPath());
            String endName = "";
            for (int q = 0; q < subPathArray.length(); q++) {
                JSONObject subPathJsonObject = subPathArray.getJSONObject(q);
                int trafficType = subPathJsonObject.getInt("trafficType");//원래 위에 선언됨
                String way = "";
                String replace = "";
                String number = "";
                String startName = "";
                int bustype = 0;
                Double startX,startY;

                if (trafficType != 3) {
                    String lane = subPathJsonObject.getString("lane");
                    String stationCount = Integer.toString(subPathJsonObject.getInt("stationCount"));
                    JSONArray laneArray = new JSONArray(lane);

                    String name = "";
                    startName = subPathJsonObject.getString("startName");
                    endName = subPathJsonObject.getString("endName");
                    startX = subPathJsonObject.getDouble("startX");
                    startY = subPathJsonObject.getDouble("startY");
                    endX = subPathJsonObject.getDouble("endX");
                    endY = subPathJsonObject.getDouble("endY");
                    for (int j = 0; j < laneArray.length(); j++) {
                        JSONObject laneObject = laneArray.getJSONObject(j);
                        if (trafficType == 2) {
                            String busNo = laneObject.getString("busNo");
                            bustype = laneObject.getInt("type");
                            if (j == 0) {
                                number = busNo;
                            } else {
                                replace = replace + " " + busNo;
                            }

                        } else if (trafficType == 1) {//지하철 경로일경우
                            String subwayCode = laneObject.getString("subwayCode");
                            number = subwayCode;
                            String passStopList = subPathJsonObject.getString("passStopList");
                            JSONObject stationsObject = new JSONObject(passStopList);
                            String stations = stationsObject.getString("stations");

                            JSONArray stationsArray = new JSONArray(stations);
                            JSONObject wayObject = stationsArray.getJSONObject(1);
                            way = wayObject.getString("stationName") + " 방면";

                        }

                    }
                    if(trafficType==2){
                        JSONObject stopPass = subPathJsonObject.getJSONObject("passStopList");
                        JSONArray stationsArray = stopPass.getJSONArray("stations");
                        JSONObject stationObject = stationsArray.getJSONObject(0);
                        stationID = stationObject.getString("stationID");
                    }
                    detailItems.add(new DetailItem(trafficType, number, startName, replace, way, stationCount + "정거장", startX, startY, bustype,stationID));

                } else {

                    String distance = "도보" + Integer.toString((int) subPathJsonObject.getDouble("distance")) + "m";
                    if (q == 0) {
                    } else {
                        DetailItem detailItem = new DetailItem(trafficType, "", endName, "", "", distance, endX, endY, 0,"");
                        detailItems.add(detailItem);
                    }

                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        DetailItemAdapter adapter = new DetailItemAdapter(getApplication().getApplicationContext(), detailItems, R.layout.detailitem);
        detailList.setAdapter(adapter);
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //아이템누르면 해당 위치에 파란마커 띄우면서 확대
                LatLng click = new LatLng(detailItems.get(position).y, detailItems.get(position).x);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(click));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
                mMap.animateCamera(zoom);

                if(detailItems.get(position).type == 2){
                    ll_busInfo.setVisibility(View.VISIBLE);
                    String busNum = detailItems.get(position).number+detailItems.get(position).replace;
                    realTimeBus(detailItems.get(position).stationID,detailItems.get(position).name,busNum);
                }
                else{
                    ll_busInfo.setVisibility(View.GONE);
                }
            }
        });


        translateBottom = AnimationUtils.loadAnimation(this,R.anim.translate_bottom);
        translateTop = AnimationUtils.loadAnimation(this,R.anim.translate_top);

        translateBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPageOpen = true;
                detailList.setClickable(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        translateTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPageOpen =false;
                detailList.setClickable(false);
                detailLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        list_recall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onoff==0) {
                    onoff=1;
                    detailLayout.setVisibility(View.VISIBLE);
                    detailLayout.startAnimation(translateBottom);
                }
            }
        });
        ///////////////////
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            downX = event.getY();
        }
        else if(event.getAction() == MotionEvent.ACTION_UP){
            upX = event.getY();
            if ((upX>downX)&&(isPageOpen==true)) {
                detailLayout.startAnimation(translateTop);
            }
            else if((upX<downX)&&(isPageOpen==false)){
                detailLayout.setVisibility(View.VISIBLE);
                detailLayout.startAnimation(translateBottom);
            }

        }

        return super.onTouchEvent(event);
    }

    String bunNumber,stationID;
    ArrayList busNumList;
    public void realTimeBus(String stationID,String startName, String bunNumber){
        this.stationID = stationID;
        String busNum[] = bunNumber.split(" ");
        busNumList = new ArrayList();
        for(int i=0;i<busNum.length;i++)
            busNumList.add(busNum[i]);
        odsayService.requestSearchStation(startName,null,null,null,
                null,null,onStationResultCallbackListener);
    }

    private OnResultCallbackListener onStationResultCallbackListener = new OnResultCallbackListener() {

        public void onSuccess(ODsayData oDsayData, API api) {
            jsonObject = oDsayData.getJson();
            try {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                JSONArray stationArray = resultObject.getJSONArray("station");
                for(int i =0;i<stationArray.length();i++){
                    JSONObject station = stationArray.getJSONObject(i);
                    String stationID_2 = station.getString("stationID");
                    //오디세이 정류장ID와 같은 ardID를 가져옴
                    if(stationID_2.equalsIgnoreCase(stationID))
                        busRouteId = station.getString("arsID");
                }
                busRouteId = busRouteId.replaceAll("-","");
                callRealBusTime(busRouteId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void onError(int i, String errorMessage, API api) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    private  void callRealBusTime(String busRouteId){
        String serviceUrl
                ="http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid";
        String serviceKey = "FDHJe6ZSsmo9hyiRMIaKvRcHzhCS%2BkXDpc%2BjoroZR47gUOD87MOdrkJdPAYlT2uLAXPzchWQ3PMRB%2BaEzGh23g%3D%3D";

        String strUrl
                = serviceUrl
                +"?ServiceKey="+serviceKey
                +"&arsId="+busRouteId;

        new DownloadWebpageTask().execute(strUrl);
    }
    private class DownloadWebpageTask extends AsyncTask<String, Void, ArrayList<BusTime>> {
        @Override
        protected ArrayList<BusTime> doInBackground(String... urls) {
            try {
                return parseXml(downloadUrl(urls[0]));
            } catch (IOException e) {
                return null;
            }
        }

        private ArrayList<BusTime> parseXml(String xmldata){
            try{
                ArrayList<BusTime> list=null;
                XmlPullParserFactory factory
                        = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);

                XmlPullParser xmlPullParser  = factory.newPullParser();

                xmlPullParser.setInput(new StringReader(xmldata));

                String busNum = "";
                String nextBus = "";
                String next2Bus = "";
                String sTag="";

                Boolean boolean_busNum=false,boolean_nextBus=false,boolean_next2Bus=false;

                int eventType = xmlPullParser.getEventType();
                BusTime bustime = null;

                while (eventType != XmlPullParser.END_DOCUMENT){

                    switch(eventType){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<BusTime>();
                            break;
                        case XmlPullParser.START_TAG:
                            sTag = xmlPullParser.getName();
                            if(sTag.equals("arrmsg1")) {
                                boolean_nextBus=true;
                            }
                            else if(sTag.equals("arrmsg2")) {
                                boolean_next2Bus=true;
                            }
                            else if(sTag.equals("rtNm")){
                                boolean_busNum=true;
                            }else if(sTag.equals("itemList")){
                                bustime = new BusTime();
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            sTag=xmlPullParser.getName();
                            if(sTag.equalsIgnoreCase("itemList")&&bustime != null){
                                list.add(bustime);
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if(boolean_busNum){
                                bustime.busNum =xmlPullParser.getText();
                                boolean_busNum=false;
                            }else if(boolean_nextBus){
                                bustime.nextBus =xmlPullParser.getText();
                                boolean_nextBus=false;
                            }else if(boolean_next2Bus){
                                bustime.next2Bus =xmlPullParser.getText();
                                boolean_next2Bus=false;
                            }
                            break;
                    }
                    eventType = xmlPullParser.next(); // 다음 parse 가르키기
                }
                return  list;
            }catch (Exception ex){
                return null;
            }
        }

        protected void onPostExecute(ArrayList<BusTime> data) {
            ll_busTime.removeAllViewsInLayout();
            for(BusTime bus : data) {
                final String busid = bus.busNum;
                final String nextBus = bus.nextBus;
                final String next2Bus = bus.next2Bus;

                for(int i =0;i<busNumList.size();i++){
                    if(busNumList.get(i).equals(busid)){

                        String busTime =busid+" "+nextBus+" "+next2Bus;
                        TextView tv_realTime = new TextView(SearchMapDetailActivity.this);
                        tv_realTime.setText(busTime);
                        tv_realTime.setLayoutParams(
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                        , ViewGroup.LayoutParams.WRAP_CONTENT));
                        ll_busTime.addView(tv_realTime);
                    }
                }
            }
        }

        private String downloadUrl (String strUrl) throws IOException {
            HttpURLConnection conn = null;
            String page = "";
            try {
                URL url = new URL(strUrl);
                conn = (HttpURLConnection) url.openConnection();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}

