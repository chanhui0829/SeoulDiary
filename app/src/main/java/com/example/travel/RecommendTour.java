package com.example.travel;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import android.support.v4.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class RecommendTour extends Fragment {

    boolean is_endoflist = false;
    int page = 1;

    ArrayList<String> content = new ArrayList<>();
    String title,addr1,firstimage,tel =null;
    String tourtypekeyword,tourtype,sigungukeyword,sigungutype = "";
    String Vcontentid,mapx,mapy,result;
    String language = "KorService";
    String keynum = "servicekey";
    Spinner spinnertype, sigungu;
    ArrayAdapter TourTypeAdapter,SigunguAdapter;

    SQLiteDatabase db;
    ImageView iv;
    DBRecommend Helper;

    ArrayList<RecommendItem> myTourItems = new ArrayList<>();//배열과 비슷한데 필요할때마다 넣다 뺏다할수있다
    RecommendItemAdapter TourAdapter = new RecommendItemAdapter(myTourItems);




    String[] list = {"Tour Type","Tourist Attractions", "Cultural Facilities", "Festivals/Events/Performan"
            , "Leisure/Sports", "Accommodation", "Shopping", "Dining", "Transportation"};
    String[] sigungulist = {"Area","Gangnam-gu","Gangdong-gu","Gangbuk-gu","Gangseo-gu","Gwanak-gu","Gwangjin-gu",
            "Guro-gu","Geumcheon-gu","Nowon-gu","Dobong-gu","Dongdaemun-gu","Dongjak-gu","Mapo-gu","Seodaemun-gu",
            "Seocho-gu","Seongdong-gu","Seongbuk-gu","Songpa-gu","Yangcheon-gu","Yongsan-gu","Eunpyeong-gu",
            "Jongno-gu","Jung-gu","Jungnang-gu"};


    String list2[] = {"관광타입","관광지","문화시설","축제공연행사","여행코스","레포츠","숙박","쇼핑","음식점"};
    String korsigungulist[] = {"지역","강남구","강동구","강북구","강서구","관악구","광진구","구로구","금천구",
            "노원구","도봉구","동대문구","동작구","마포구","서대문구","서초구","성동구","성북구","송파구","양천구",
            "영등포구","용산구","은평구","종로구","중구","중랑구"};

    ListView listView;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.activity_recommendtour,null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar); //툴바설정
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("RecommendTour");
        setHasOptionsMenu(true);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if(Home.languagetype.equals("0")){
            language = "KorService";        }
        else if(Home.languagetype.equals("1")){
            language = "EngService";
        }

        iv = (ImageView)view.findViewById(R.id.img_reset);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle(getString(R.string.reset));
                alertDialogBuilder
                        .setMessage(getString(R.string.reset2))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.resetno),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton(getString(R.string.resetyes),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        // 프로그램을 종료한다
                                        Helper = new DBRecommend(getContext());
                                        db = Helper.getWritableDatabase();
                                        db.execSQL("delete from recommendcos");

                                        onRefresh();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            }
        });
        listView = (ListView)view.findViewById(R.id.layout_list);
        spinnertype = (Spinner) view.findViewById(R.id.tourtype);


        if (language.equals("KorService")) {
            TourTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list2);
        }
        else {
            TourTypeAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, list);
        }
        spinnertype.setAdapter(TourTypeAdapter);
        tourtype = spinnertype.getSelectedItem().toString();


        sigungu = (Spinner) view.findViewById(R.id.sigungu);
        if (language.equals("KorService")) {
            SigunguAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, korsigungulist);
        }
        else{
            SigunguAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, sigungulist);
        }
        sigungu.setAdapter(SigunguAdapter);
        sigungutype = sigungu.getSelectedItem().toString();



        spinnertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                page=1;
                myTourItems.clear();
                TourAdapter.notifyDataSetChanged();

                if(language.equals("KorService")){
                    if (position == 0) tourtypekeyword = "";
                    else if (position == 1) tourtypekeyword = "12";
                    else if (position == 2) tourtypekeyword = "14";
                    else if (position == 3) tourtypekeyword = "15";
                    else if (position == 4) tourtypekeyword = "25";
                    else if (position == 5) tourtypekeyword = "28";
                    else if (position == 6) tourtypekeyword = "32";
                    else if (position == 7) tourtypekeyword = "38";
                    else if (position == 8) tourtypekeyword = "39";
                } else {
                    if (position == 0) tourtypekeyword = "";
                    else if (position == 1) tourtypekeyword = "76";
                    else if (position == 2) tourtypekeyword = "78";
                    else if (position == 3) tourtypekeyword = "85";
                    else if (position == 4) tourtypekeyword = "75";
                    else if (position == 5) tourtypekeyword = "80";
                    else if (position == 6) tourtypekeyword = "79";
                    else if (position == 7) tourtypekeyword = "82";
                    else if (position == 8) tourtypekeyword = "77";
                }

                getJSON();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });



        listView.setFocusable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), RecommendTourDetail.class);
                intent.putExtra("keyword",tourtypekeyword);
                intent.putExtra("contentid",myTourItems.get(position).contentid);
                intent.putExtra("language",language);
                startActivity(intent);

            }
        });



        sigungu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                myTourItems.clear();
                TourAdapter.notifyDataSetChanged();
                page=1;
                if(position==0){
                    sigungukeyword = "" ;
                }
                else {
                    sigungukeyword = String.valueOf(position);
                }
                getJSON();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { // res/menu 에서 친구 탭에서 작동 할 menu를 가져온다.
        inflater.inflate(R.menu.recommendtool,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bt2:
                Intent movemap = new Intent(getActivity(), BookMarkMap.class);
                startActivity(movemap);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public static RecommendTour newInstance() {
        return new RecommendTour();
    }

    public void onRefresh() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frameLayout, RecommendTour.newInstance()).commitAllowingStateLoss();
    }



    public void getJSON() {

        result=null;

        new Thread() {

            public void run() {
                try {

                    String url2 = "http://api.visitkorea.or.kr/openapi/service/rest/"+language+"/areaBasedList?" +
                            "ServiceKey="+keynum+"&MobileOS=ETC&MobileApp=appName&areaCode=1&sigunguCode="
                            +sigungukeyword+"&contentTypeId="+tourtypekeyword+"&pageNo="+page+"&numOfRows=10&_type=json";

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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsonParser(result);

            }
        });
    }

    public boolean jsonParser(String jsonString){

        if (jsonString == null ) return false;

        try {

            JSONObject json = new JSONObject(jsonString);
            JSONObject requestjson = json.getJSONObject("response");
            JSONObject bodyjson = requestjson.getJSONObject("body");

            JSONObject items = bodyjson.getJSONObject("items");


            String totalCount = bodyjson.getString("totalCount");
            final String pageNo = bodyjson.getString("pageNo");
            final int tc = Integer.parseInt(totalCount);
            int pn = Integer.parseInt(pageNo);


            if ((tc% pn == 1 && page == pn) ||((tc == 1)&&(pn==1))){

                JSONObject item = items.getJSONObject("item");

                if (!(item.isNull("firstimage"))) {
                    firstimage = item.getString("firstimage");
                } else {
                    firstimage = "R.drawable.image";
                }

                if (!(item.isNull("title"))) {
                    title = item.getString("title");
                    title = title.replaceAll("<br>","");
                } else {
                    title = "";
                }

                if (!(item.isNull("addr1"))) {
                    addr1 = item.getString("addr1");
                    addr1 = addr1.replaceAll("<br>","");
                } else {
                    addr1 = "";
                }

                if (!(item.isNull("tel"))) {
                    tel = item.getString("tel");


                } else {
                    tel = " ";
                }
                Vcontentid = item.getString("contentid");
                mapx = item.getString("mapx");
                mapy = item.getString("mapy");

                RecommendItem myitem1 = new RecommendItem(title, addr1, firstimage, Vcontentid, tel, mapx, mapy);//new MyItem(이부분 수정)
                myTourItems.add(myitem1);
                listView.setAdapter(TourAdapter);


            } else {

                JSONArray jArr = items.getJSONArray("item");

                for (int i = 0; i < jArr.length(); i++) {

                    JSONObject tourInfo = jArr.getJSONObject(i);

                    if (!(jArr.getJSONObject(i).isNull("title"))) {
                        title = tourInfo.getString("title");
                    } else {
                        title = "";
                    }


                    if (!(jArr.getJSONObject(i).isNull("tel"))) {
                        tel = tourInfo.getString("tel");
                    } else {
                        tel = " ";
                    }


                    if (!(jArr.getJSONObject(i).isNull("addr1"))) {
                        addr1 = tourInfo.getString("addr1");

                    } else {
                        addr1 = "";

                    }


                    if (!(jArr.getJSONObject(i).isNull("firstimage"))) {
                        firstimage = tourInfo.getString("firstimage");
                    } else {
                        firstimage = "R.drawable.image";
                    }

                    /////////////////////////////////////////////////////////////////////


                    Vcontentid = null;
                    Vcontentid = tourInfo.getString("contentid");
                    mapx = tourInfo.getString("mapx");
                    mapy = tourInfo.getString("mapy");


                    final RecommendItem myitem1 = new RecommendItem(title, addr1, firstimage, Vcontentid,tel,mapx,mapy);//new MyItem(이부분 수정)

                    myTourItems.add(myitem1);
                    listView.setAdapter(TourAdapter);

                }


            }

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    is_endoflist = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                }

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && is_endoflist) {
                        page++;
                        //스크롤 위치 저장
                        int fVisible = listView.getFirstVisiblePosition();
                        View vFirst = listView.getChildAt(0);
                        int pos = 0;
                        if (vFirst != null) pos = vFirst.getTop();
                        getJSON();


                        TourAdapter.notifyDataSetChanged();
                        listView.setSelectionFromTop(fVisible, pos);
                    }

                }

            });

        }catch (JSONException e) {

        }



        return false;
    }

}

