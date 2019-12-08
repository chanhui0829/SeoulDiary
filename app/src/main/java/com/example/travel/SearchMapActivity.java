package com.example.travel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import static android.view.View.VISIBLE;


public class SearchMapActivity extends Fragment {



    InputMethodManager imm;

    private Button btn_call, search_bus, search_subway, search_subbus, search_all;
    private TextView tv_total;
    private TextView noresult;
    private EditText et_mapStart;
    private EditText et_mapEnd;
    private JSONArray jsonArray;

    static JSONArray conjsonArray;
    static ArrayList<MyItem> conItems;
    static String start_X, start_Y, end_X, end_Y;

    private ODsayService odsayService;
    private JSONObject jsonObject;
    private ArrayList<MyItem> myItems = new ArrayList<>();
    private ArrayList<ChangeItem> changeItems = new ArrayList<>();

    private ArrayList<MyItem> busItems = new ArrayList<>();
    private ArrayList<MyItem> subbusItems = new ArrayList<>();
    private ArrayList<MyItem> subwayItems = new ArrayList<>();
    private ListView listView;

    private String startName;
    private String endName;
    GpsTracker gpsTracker;

    public String getCurrentAddress(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            Toast.makeText(getActivity(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_map, null);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("Public Transport");

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        btn_call = view.findViewById(R.id.btn_call);


        noresult = view.findViewById(R.id.noresult);
        et_mapEnd = view.findViewById(R.id.et_mapEnd);
        et_mapStart = view.findViewById(R.id.et_mapStart);
        search_bus = view.findViewById(R.id.search_bus);
        search_subbus = view.findViewById(R.id.search_subbus);
        search_subway = view.findViewById(R.id.search_subway);
        search_all = view.findViewById(R.id.search_all);

        search_all.setVisibility(View.GONE);
        search_subbus.setVisibility(View.GONE);
        search_bus.setVisibility(View.GONE);
        search_subway.setVisibility(View.GONE);

        odsayService = ODsayService.init(getContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);

        listView = view.findViewById(R.id.maplist);


        gpsTracker = new GpsTracker(getActivity());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        et_mapStart.setText(address);


        btn_call.setOnClickListener(onClickListener);

        search_all.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                conjsonArray = jsonArray;
                conItems = myItems;
                MyItemAdapter adapter = new MyItemAdapter(getActivity().getApplicationContext(), myItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getContext(),SearchMapDetailActivity.class);
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
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
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
                conjsonArray = jsonArray;
                conItems = busItems;
                MyItemAdapter adapter = new MyItemAdapter(getActivity().getApplicationContext(), busItems, R.layout.myitem);
                listView.removeAllViewsInLayout();
                listView.setOnItemClickListener(null);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getContext(),SearchMapDetailActivity.class);
                            String mapObj = busItems.get(position).mapObj;
                            String passStopList;
                            int i = busItems.get(position).position;
                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
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
                conjsonArray = jsonArray;
                conItems = subwayItems;
                MyItemAdapter adapter = new MyItemAdapter(getActivity().getApplicationContext(), subwayItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getContext(),SearchMapDetailActivity.class);
                            String mapObj = subwayItems.get(position).mapObj;
                            int i = subwayItems.get(position).position;
                            String passStopList;

                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
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
                conjsonArray = jsonArray;
                conItems = subbusItems;
                MyItemAdapter adapter = new MyItemAdapter(getActivity().getApplicationContext(), subbusItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getContext(),SearchMapDetailActivity.class);
                            String mapObj = subbusItems.get(position).mapObj;
                            String passStopList;
                            int i = subbusItems.get(position).position;
                            JSONObject subJsonObject = jsonArray.getJSONObject(i);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName", startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return view;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            hideKeyboard();
            final Geocoder geocoder = new Geocoder(getContext());
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
                Toast.makeText(getContext(), "출발지를 입력해주세요", Toast.LENGTH_LONG).show();
            } else if(endName.equals("")){
                Toast.makeText(getContext(), "도착지를 입력해주세요", Toast.LENGTH_LONG).show();
            } else if (start.isEmpty()) {
                Toast.makeText(getContext(), "출발지에 해당되는 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
            }else if (end.isEmpty()) {
                Toast.makeText(getContext(), "도착지에 해당되는 주소 정보가 없습니다", Toast.LENGTH_LONG).show();
            } else {
                start_X = Double.toString(start.get(0).getLongitude());
                start_Y = Double.toString(start.get(0).getLatitude());
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

                jsonArray = pathObject.getJSONArray("path");
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

                conjsonArray = jsonArray;
                conItems = myItems;
                MyItemAdapter adapter = new MyItemAdapter(getActivity().getApplicationContext(), myItems, R.layout.myitem);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            Intent intent = new Intent(getContext(),SearchMapDetailActivity.class);
                            String mapObj = myItems.get(position).mapObj;
                            String passStopList;

                            JSONObject subJsonObject = jsonArray.getJSONObject(position);
                            passStopList = subJsonObject.toString();
                            intent.putExtra("mapObj", mapObj);
                            intent.putExtra("passStopList", passStopList);
                            intent.putExtra("startName",startName);
                            intent.putExtra("endName", endName);
                            intent.putExtra("startXY", start_X+" "+start_Y);
                            intent.putExtra("endXY", end_X+" "+end_Y);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        public void onError(int i, String errorMessage, API api) {
            Toast.makeText(getContext(), "검색 결과가 없습니다", Toast.LENGTH_LONG).show();
            noresult.setVisibility(VISIBLE);
        }
    };


    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(et_mapStart.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(et_mapEnd.getWindowToken(), 0);
    }



}