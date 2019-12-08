package com.example.travel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.VISIBLE;



public class RecommendSearchMapActivity extends AppCompatActivity {

    InputMethodManager imm;

    private Button btn_call, search_bus, search_subway, search_subbus, search_all;
    private TextView tv_total;
    private TextView noresult;
    private EditText et_mapStart;
    private EditText et_mapEnd;
    private JSONArray jsonArray;//이놈
    static String start_X, start_Y, end_X, end_Y;
    static JSONArray conjsonArray2;
    static ArrayList<MyItem> conItems2;

    private Context context;

    private ODsayService odsayService;
    private JSONObject jsonObject;
    private ArrayList<MyItem> myItems = new ArrayList<>();//배열과 비슷한데 필요할때마다 넣다 뺏다할수있다
    private ArrayList<ChangeItem> changeItems = new ArrayList<>();//

    private ArrayList<MyItem> busItems = new ArrayList<>();//버스 루트 배열
    private ArrayList<MyItem> subbusItems = new ArrayList<>();//
    private ArrayList<MyItem> subwayItems = new ArrayList<>();//
    private ListView listView;

    private String startName;
    private String endName;

    private static final int REQ_CODE_SPEECH_INPUT = 100;


    /**
     * google place api 를 위한 변수들 START
     **/

    private GoogleApiClient googleApiClient;

    private RecyclerView rvAutocomplateKeyword;

    private LinearLayoutManager llm;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(

            new LatLng(-0, 0), new LatLng(0, 0));

    private EditText edSearch = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map2);


        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
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
                Intent homelogo = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(homelogo);
            }
        });


        btn_call = findViewById(R.id.btn_call);
        tv_total = findViewById(R.id.tv_total);

        noresult = findViewById(R.id.noresult);
        et_mapEnd = findViewById(R.id.et_mapEnd);
        et_mapStart = findViewById(R.id.et_mapStart);
        search_bus = findViewById(R.id.search_bus);
        search_subbus = findViewById(R.id.search_subbus);
        search_subway = findViewById(R.id.search_subway);
        search_all = findViewById(R.id.search_all);

        search_all.setVisibility(View.GONE);
        search_subbus.setVisibility(View.GONE);
        search_bus.setVisibility(View.GONE);
        search_subway.setVisibility(View.GONE);

        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        listView = findViewById(R.id.maplist);
        Intent intent = getIntent();
        String addr = intent.getStringExtra("addr");

        et_mapEnd.setText(addr);
        btn_call.setOnClickListener(onClickListener);
        search_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conjsonArray2 = jsonArray;
                conItems2 = myItems;
                MyItemAdapter2 adapter = new MyItemAdapter2(getApplicationContext(), myItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), SearchMapDetailActivity.class);
                            String mapObj = myItems.get(position).mapObj;
                            String passStopList;
                            int i = myItems.get(position).position;
                            //subpath의 정보 전달
                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X + " " + start_Y);
                            intent.putExtra("endXY", end_X + " " + end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        search_bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conjsonArray2 = jsonArray;
                conItems2 = busItems;
                MyItemAdapter2 adapter = new MyItemAdapter2(getApplicationContext(), busItems, R.layout.myitem);
                listView.removeAllViewsInLayout();
                listView.setOnItemClickListener(null);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), SearchMapDetailActivity.class);
                            String mapObj = busItems.get(position).mapObj;
                            String passStopList;
                            int i = busItems.get(position).position;
                            //subpath의 정보 전달
                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X + " " + start_Y);
                            intent.putExtra("endXY", end_X + " " + end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        search_subway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conjsonArray2 = jsonArray;
                conItems2 = subwayItems;
                MyItemAdapter2 adapter = new MyItemAdapter2(getApplicationContext(), subwayItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), SearchMapDetailActivity.class);
                            String mapObj = subwayItems.get(position).mapObj;
                            int i = subwayItems.get(position).position;
                            String passStopList;

                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X + " " + start_Y);
                            intent.putExtra("endXY", end_X + " " + end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        search_subbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conjsonArray2 = jsonArray;
                conItems2 = subbusItems;
                MyItemAdapter2 adapter = new MyItemAdapter2(getApplicationContext(), subbusItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), SearchMapDetailActivity.class);
                            String mapObj = subbusItems.get(position).mapObj;
                            String passStopList;
                            int i = subbusItems.get(position).position;
                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X + " " + start_Y);
                            intent.putExtra("endXY", end_X + " " + end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }




    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            hideKeyboard();
            final Geocoder geocoder = new Geocoder(getApplicationContext());
            List<Address> start = null;
            List<Address> end = null;
            startName = et_mapStart.getText().toString();
            endName = et_mapEnd.getText().toString();

            try {
                start = geocoder.getFromLocationName(
                        startName,
                        10);
                end = geocoder.getFromLocationName(
                        endName,
                        10);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(startName.equals("")){
                Toast.makeText(getApplicationContext(), "출발지를 입력해주세요", Toast.LENGTH_LONG).show();
            } else if(endName.equals("")){
                Toast.makeText(getApplicationContext(), "도착지를 입력해주세요", Toast.LENGTH_LONG).show();
            } else if (start.isEmpty()) {
                Toast.makeText(getApplicationContext(), "출발지에 해당되는 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
            }else if (end.isEmpty()) {
                Toast.makeText(getApplicationContext(), "도착지에 해당되는 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                start_X = Double.toString(start.get(0).getLongitude());        // 위도
                start_Y = Double.toString(start.get(0).getLatitude());    // 경도
                end_X = Double.toString(end.get(0).getLongitude());
                end_Y = Double.toString(end.get(0).getLatitude());
                odsayService.requestSearchPubTransPath(start_X, start_Y, end_X, end_Y, "0", "0", "0", onResultCallbackListener);
            }
        }
    };

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {

        public void onSuccess(ODsayData oDsayData, API api) {
            search_all.setVisibility(VISIBLE);
            search_bus.setVisibility(VISIBLE);
            search_subbus.setVisibility(VISIBLE);
            search_subway.setVisibility(VISIBLE);
            jsonObject = oDsayData.getJson();

            try {
                noresult.setVisibility(View.GONE);
                JSONObject pathObject = jsonObject.getJSONObject("result");

                final int subwayBusCount = Integer.parseInt(pathObject.getString("subwayBusCount"));
                int busCount = Integer.parseInt(pathObject.getString("busCount"));
                int subwayCount = Integer.parseInt(pathObject.getString("subwayCount"));
                int total = subwayBusCount + busCount + subwayCount;

                int trafficType;
                String lane;
                final String info = "";
                myItems.clear();
                busItems.clear();
                subbusItems.clear();
                subwayItems.clear();

                tv_total.setText("총 경로수 : " + total);
                jsonArray = pathObject.getJSONArray("path");
                //테스트용 코드
                int iiii = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject subJsonObject = jsonArray.getJSONObject(i);
                    TrafficInfoResult sub = new TrafficInfoResult(subJsonObject);
                    JSONArray subPathArray = new JSONArray(sub.getSubPath());
                    int pathType = sub.getPathType();

                    String mapObj = sub.getMapObj();
                    changeItems = new ArrayList<>();

                    for (int q = 0; q < subPathArray.length(); q++) {

                        JSONObject subPathJsonObject = subPathArray.getJSONObject(q);
                        trafficType = subPathJsonObject.getInt("trafficType");
                        String replace = "";
                        String number = "";
                        String startName = "";
                        String endName = "";
                        if (trafficType != 3) {
                            lane = subPathJsonObject.getString("lane");
                            JSONArray laneArray = new JSONArray(lane);
                            for (int j = 0; j < laneArray.length(); j++) {
                                JSONObject laneObject = laneArray.getJSONObject(j);
                                if (trafficType == 2) {
                                    String busNo = laneObject.getString("busNo");
                                    if (j == 0) {
                                        number = busNo;
                                    } else {
                                        replace = replace + " " + busNo;
                                    }
                                } else if (trafficType == 1) {
                                    String subwayCode = laneObject.getString("subwayCode");
                                    number = subwayCode;
                                }
                            }
                            String passStopList = subPathJsonObject.getString("passStopList");
                            JSONObject stationsObject = new JSONObject(passStopList);
                            String stations = stationsObject.getString("stations");
                            JSONArray stationsArray = new JSONArray(stations);

                            String name = "";
                            startName = subPathJsonObject.getString("startName");
                            endName = subPathJsonObject.getString("endName");

                            ChangeItem changeItem_start = new ChangeItem(trafficType, number, startName, replace);
                            ChangeItem changeItem_end = new ChangeItem(0, "", endName, "");
                            changeItems.add(changeItem_start);
                            changeItems.add(changeItem_end);
                        }
                    }
                    MyItem myitem1 = new MyItem(sub.getPayment(),mapObj, sub.getTotalTime(), sub.getTotalWalk(), changeItems,i);//new MyItem(이부분 수정)
                    myItems.add(myitem1);
                    if (pathType == 1) {
                        subwayItems.add(myitem1);
                    } else if (pathType == 2) {
                        busItems.add(myitem1);
                    } else {
                        subbusItems.add(myitem1);
                    }
                }///////////////

                conjsonArray2 = jsonArray;
                conItems2 = myItems;
                MyItemAdapter2 adapter = new MyItemAdapter2(getApplicationContext(), myItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getApplicationContext(),SearchMapDetailActivity.class);
                            String mapObj = myItems.get(position).mapObj;
                            String passStopList;

                            JSONObject subJsonObject = jsonArray.getJSONObject(position);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName",startName);
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        public void onError(int i, String errorMessage, API api) {
            Toast.makeText(getApplicationContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
            noresult.setVisibility(VISIBLE);
        }
    };


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



    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(et_mapStart.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_mapEnd.getWindowToken(), 0);
    }


}