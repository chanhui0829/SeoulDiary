package com.example.travel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Context.LOCATION_SERVICE;

public class Home extends Fragment {
    int resID;
    static String languagetype ="0" ;

    TextView txt_weather, txt_temp;
    Button btn_make;
    SQLiteDatabase db;

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(getActivity(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        Context context = getActivity();
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////

    class Weather extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... address) {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch = (char) data;
                    content = content + ch;
                    data = isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, null);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(R.drawable.homelogo);
        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginStart(50);
        toolbar.setTitle("Home");
        setHasOptionsMenu(true);

        btn_make = (Button)view.findViewById(R.id.btn_make);
        btn_make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), travel.class);
                startActivity(intent);
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        DBRecommend Helper = new DBRecommend(inflater.getContext());
        db = Helper.getWritableDatabase();


        String sql  = "select * from language";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String mapnum = cursor.getString(0);
                languagetype = mapnum;
            }
        }else{
            db.execSQL("INSERT INTO language values ('0');");
            languagetype ="0";
        }

        Locale lang = Locale.KOREA;
        if(languagetype.equals("0")){
            lang = Locale.KOREA;

        }else if(languagetype.equals("1")){
            lang = Locale.US;
        }
        Configuration config = new Configuration();
        config.locale = lang;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        final TextView textview_address = (TextView) view.findViewById(R.id.textview);
        ImageView img_weather2 = (ImageView) view.findViewById(R.id.img_weather);


        gpsTracker = new GpsTracker(getActivity());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        String address = getCurrentAddress(latitude, longitude);
        textview_address.setText(address);


        ///////////////////////////////////
        txt_weather = view.findViewById(R.id.txt_weather);
        txt_temp = view.findViewById(R.id.txt_temp);


        String content;


        Weather weather = new Weather();

        try {
            content = weather.execute("https://openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=apikey").get();


            JSONObject jsonObject = new JSONObject(content);
            String weatherData = ((JSONObject) jsonObject).getString("weather");
            String mainTemperature = jsonObject.getString("main");


            JSONArray array = new JSONArray(weatherData);

            String main = "";
            String icon = "";
            String id = "";
            String temperature = "";

            for (int i = 0; i < array.length(); i++) {
                JSONObject weatherPart = array.getJSONObject(i);
                main = weatherPart.getString("main");
                icon = weatherPart.getString("icon");
                id = weatherPart.getString("id");
            }

            resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());


            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");
            Double temp = Double.parseDouble(temperature);
            long temp2 = Math.round(temp);
            txt_temp.setText(String.valueOf(temp2) + "°C");

            if(languagetype.equals("0")){
                if(main.equals("Clear")){
                    txt_weather.setText("맑음");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                } else if(main.equals("Thunderstrom")){
                    txt_weather.setText("뇌우");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                } else if(main.equals("Drizzle")){
                    txt_weather.setText("이슬비");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                } else if(main.equals("Rain")){
                    txt_weather.setText("비");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                }else if(main.equals("Snow")){
                    txt_weather.setText("눈");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                }else if(main.equals("Clouds")) {
                    txt_weather.setText("흐림");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                }else if(main.equals("Mist")) {
                    txt_weather.setText("안개");
                    resID = getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                } else if(id.equals("701")){
                    txt_weather.setText("안개");
                    resID = getResources().getIdentifier("@drawable/ic_fog", "drawable", getActivity().getPackageName());
                }else if(id.equals("711")){
                    txt_weather.setText("안개");
                    resID = getResources().getIdentifier("@drawable/ic_fog", "drawable", getActivity().getPackageName());
                }else if(id.equals("721")){
                    txt_weather.setText("안개");
                    resID = getResources().getIdentifier("@drawable/ic_fog", "drawable", getActivity().getPackageName());
                }else if(id.equals("731")){
                    txt_weather.setText("황사");
                    resID = getResources().getIdentifier("@drawable/ic_50d", "drawable", getActivity().getPackageName());
                }else if(id.equals("741")){
                    txt_weather.setText("안개");
                    resID = getResources().getIdentifier("@drawable/ic_fog", "drawable", getActivity().getPackageName());
                }else if(id.equals("751")){
                    txt_weather.setText("황사");
                    resID = getResources().getIdentifier("@drawable/ic_50d", "drawable", getActivity().getPackageName());
                }else if(id.equals("761")) {
                    txt_weather.setText("미세먼지");
                    resID = getResources().getIdentifier("@drawable/ic_50d", "drawable", getActivity().getPackageName());
                }else if(id.equals("762")) {
                    txt_weather.setText("화산재");
                    resID = getResources().getIdentifier("@drawable/ic_50d", "drawable", getActivity().getPackageName());
                }else if(id.equals("771")) {
                    txt_weather.setText("돌풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("751")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("900")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("901")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                } else if(id.equals("902")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("903")) {
                    txt_weather.setText("한랭");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("904")) {
                    txt_weather.setText("무더위");
                    resID = getResources().getIdentifier("@drawable/ic_01d" , "drawable", getActivity().getPackageName());
                }else if(id.equals("906")) {
                    txt_weather.setText("눈");
                    resID = getResources().getIdentifier("@drawable/ic_13d" , "drawable", getActivity().getPackageName());
                }else if(id.equals("952")) {
                    txt_weather.setText("미풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("953")) {
                    txt_weather.setText("미풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("954")) {
                    txt_weather.setText("미풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("955")) {
                    txt_weather.setText("미풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("956")) {
                    txt_weather.setText("미풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("958")) {
                    txt_weather.setText("강풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("959")) {
                    txt_weather.setText("강풍");
                    resID = getResources().getIdentifier("@drawable/ic_wind" , "drawable", getActivity().getPackageName());
                }else if(id.equals("960")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("961")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("962")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                }else if(id.equals("50d")) {
                    txt_weather.setText("태풍");
                    resID = getResources().getIdentifier("@drawable/ic_tornado" , "drawable", getActivity().getPackageName());
                } else{
                    txt_weather.setText(main);
                    getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
                }
            }else {
                txt_weather.setText(main);
                getResources().getIdentifier("@drawable/ic_" + icon, "drawable", getActivity().getPackageName());
            }
            img_weather2.setImageResource(resID);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { // res/menu 에서 친구 탭에서 작동 할 menu를 가져온다.
        inflater.inflate(R.menu.languagetool, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_language:
                CharSequence language[] = new CharSequence[] {"한국어", "English" };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Language");
                builder.setItems(language, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {


                        Locale lang = Locale.KOREA;

                        if(whichButton == 0){
                            lang = Locale.KOREA;
                            db.execSQL("update language set lang = '0';");
                            languagetype = "0";

                            onRefresh();
                        }else if(whichButton ==1){
                            lang = Locale.US;
                            db.execSQL("update language set lang = '1';");
                            languagetype = "1";
                            onRefresh();
                        }

                        Configuration config = new Configuration();
                        config.locale = lang;
                        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

                        dialog.dismiss();
                    }

                });

                builder.show();

        }
        return super.onOptionsItemSelected(item) ;
    }

    public static Home newInstance() {
        return new Home();
    }

    public void onRefresh() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.frameLayout, Home.newInstance()).commitAllowingStateLoss();

    }


}


