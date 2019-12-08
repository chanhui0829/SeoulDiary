package com.example.travel;


import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.travel.ListViewSize.BitmapToString;
import static com.example.travel.ListViewSize.StringToBitmap;


public class travel
        extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Button btn_startend, delete, btn_post;
    int inser,insermode = 0;
    Marker insermarker;
    double inserx,insery = 0;
    EditText dlog_memo, dlog_title;
    ImageView dlog_iv;
    Button btn_yes, btn_no;
    LinearLayout dlog_layout,noclicklayout;
    private static int PICK_IMAGE_REQUEST = 1;
    String img_bitmap ="";
    Bitmap scaled;
    double dlog_lat,dlog_long;
    RadioGroup memo_select;
    ArrayList<String> memo_title = new ArrayList<>();
    ArrayList<String> memo_memo = new ArrayList<>();
    ArrayList<String> memo_check = new ArrayList<>();
    ArrayList<String> memo_Time = new ArrayList<>();
    ArrayList<String> memo_bitmap = new ArrayList<>();
    ArrayList<Double> memo_x = new ArrayList<>();
    ArrayList<Double> memo_y = new ArrayList<>();

    //
    int toggle;
    GoogleMap mMap;
    double latitude,longitude;

    private PolylineOptions polylineOptions;
    private ArrayList<LatLng> arrayPoints;
    private ArrayList<LatLng> markerPoints;
    ArrayList<Double> llx = new ArrayList<>();
    ArrayList<Double> lly = new ArrayList<>();
    SQLiteDatabase db,namedb,memodb;

    public DBHelperMap mapdb;
    public DBHelperMapName mapnamedb;
    public DBHelperMapMemo mapmemodb;



    @Override
    public void onMapReady(final GoogleMap map) {

        mMap = map;
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar); //툴바설정
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



        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btn_startend = (Button)findViewById(R.id.btn_startend);
        delete=findViewById(R.id.btn_save);
        btn_post=findViewById(R.id.btn_post);
        toggle = 0;
        markerPoints = new ArrayList<LatLng>();


        dlog_memo=findViewById(R.id.memoedit3);
        dlog_title=findViewById(R.id.nameedit3);
        dlog_iv=findViewById(R.id.dlog_iv);
        btn_yes=findViewById(R.id.btn_yes);
        btn_no=findViewById(R.id.btn_no);
        noclicklayout=findViewById(R.id.noclickView);
        dlog_layout=findViewById(R.id.dlog_layout);
        memo_select=findViewById(R.id.memo_select);

        dlog_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        noclicklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlog_iv.setImageResource(R.drawable.ic_launcher_foreground);
                dlog_title.setText(null);
                dlog_memo.setText(null);
                img_bitmap=null;
                dlog_layout.setVisibility(View.GONE);
                noclicklayout.setVisibility(View.GONE);
                memo_select.check(R.id.priority1);
                inser=0;
                insermode=0;
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dlog_title.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_titlehint,Toast.LENGTH_SHORT).show();
                }else if(dlog_memo.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_contenthint,Toast.LENGTH_SHORT).show();
                }else if(dlog_title.getText().toString().equals("현재위치")||dlog_title.getText().toString().equals("Mylocation")||
                        dlog_title.getText().toString().equals("시작")||dlog_title.getText().toString().equals("departure")||
                        dlog_title.getText().toString().equals("끝")||dlog_title.getText().toString().equals("arrive")){
                    Toast.makeText(getApplicationContext(),R.string.tr_memo_notitle,Toast.LENGTH_SHORT).show();
                }else {
                    RadioButton rd = (RadioButton) findViewById(memo_select.getCheckedRadioButtonId());
                    String check = rd.getText().toString();
                    if (insermode == 1) {
                        memo_title.set(inser, dlog_title.getText().toString());
                        memo_memo.set(inser, dlog_memo.getText().toString());
                        memo_check.set(inser, check);
                        memo_bitmap.set(inser, img_bitmap);
                        insermarker.remove();

                        MarkerOptions markerOptions = new MarkerOptions();
                        BitmapDrawable bitmapdraw2;
                        bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.favoritemark);


                        Bitmap b=bitmapdraw2.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));///////////
                        LatLng mark = new LatLng(insery, inserx);//배열에서 i번째 값 받아와서 저장
                        markerOptions.position(mark).title(dlog_title.getText().toString());
                        mMap.addMarker(markerOptions);

                    } else {
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        String getTime = sdf.format(date);

                        memo_title.add(dlog_title.getText().toString());
                        memo_memo.add(dlog_memo.getText().toString());
                        memo_check.add(check);
                        memo_Time.add(getTime);
                        memo_bitmap.add(img_bitmap);

                        memo_x.add(dlog_long);
                        memo_y.add(dlog_lat);

                        MarkerOptions markerOptions = new MarkerOptions();
                        BitmapDrawable bitmapdraw2;
                        bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.favoritemark);


                        Bitmap b=bitmapdraw2.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));///////////
                        LatLng mark = new LatLng(dlog_lat, dlog_long);//배열에서 i번째 값 받아와서 저장
                        markerOptions.position(mark).title(dlog_title.getText().toString());
                        mMap.addMarker(markerOptions);
                    }


                    //이미지 초기화, 글자 2개 없애기, 라디오 버튼 초기화 해야함
                    dlog_iv.setImageResource(R.drawable.ic_launcher_foreground); //이거에 기본이미지 하나 넣어서 초기화시켜야함
                    dlog_title.setText(null);
                    dlog_memo.setText(null);
                    img_bitmap = null;
                    dlog_layout.setVisibility(View.GONE);
                    noclicklayout.setVisibility(View.GONE);
                    memo_select.check(R.id.priority1);
                    inser = 0;
                    insermode = 0;
                }
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지 초기화, 글자 2개 없애기, 라디오 버튼 초기화 해야함
                dlog_iv.setImageResource(R.drawable.ic_launcher_foreground); //이거에 기본이미지 하나 넣어서 초기화시켜야함
                dlog_title.setText(null);
                dlog_memo.setText(null);
                img_bitmap=null;
                dlog_layout.setVisibility(View.GONE);
                noclicklayout.setVisibility(View.GONE);
                memo_select.check(R.id.priority1);
                inser=0;
                insermode=0;
            }
        });
        ////////////////////////////////////////////////////////////////////////////////////////dlog Onceate는 여기까지

        mapdb = new DBHelperMap(this,"map.db",null,2);
        mapnamedb = new DBHelperMapName(this, "mapname.db",null,2);
        mapmemodb = new DBHelperMapMemo(this, "mapmemo.db",null,2);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        arrayPoints = new ArrayList<LatLng>();

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle!=0&&llx.size()!=0) {
                    boolean overlap = true;

                    for(int i = 0; i < memo_x.size();i++){
                        if(memo_x.get(i) == longitude && memo_y.get(i) == latitude){
                            overlap=false;
                        }
                    }
                    if(overlap) {
                        dlog_layout.setVisibility(View.VISIBLE);
                        noclicklayout.setVisibility(View.VISIBLE);
                        dlog_lat = latitude;
                        dlog_long = longitude;
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.tr_memonostart,Toast.LENGTH_SHORT).show();
                    }

                }else if(llx.size()==0){
                    Toast.makeText(getApplicationContext(),R.string.tr_memonostart3,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),R.string.tr_memonostart2,Toast.LENGTH_SHORT).show();
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {//저장버튼
            @Override
            public void onClick(View v) {
                if(toggle==0&&llx.size()!=0) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(travel.this);
                    ad.setTitle(getString(R.string.tr_savecontent));
                    ad.setMessage("");

                    final EditText et = new EditText(travel.this);
                    ad.setView(et);
                    ad.setPositiveButton(R.string.tr_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String value = et.getText().toString();
                            arrayPoints.clear();
                            markerPoints.clear();
                            mMap.clear();
                            namedb=mapnamedb.getWritableDatabase();

                            String sql = "select MAX(number) from mapnametb;";

                            int mapnum = 0;

                            Cursor result = namedb.rawQuery(sql, null);

                            if (result.moveToFirst()) {
                                int id = result.getInt(0);

                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                                String getTime = sdf.format(date);

                                mapnum=id+1;
                                String sql3;
                                sql3 = String.format("INSERT INTO mapnametb VALUES("+mapnum+",'"+value+"','"+getTime+"');");
                                namedb.execSQL(sql3);
                            }
                            result.close();




                            db = mapdb.getWritableDatabase();
                            String sql2;
                            for (int i = 0; i < lly.size(); i++) {
                                sql2 = String.format("INSERT INTO maptb VALUES(" + mapnum + " ," + i + "," + llx.get(i) + "," + lly.get(i) + ");");//null부분에 지도맵(리스트에서 사용할) 넘버 넣어야함
                                db.execSQL(sql2);
                            }

                            llx.clear();
                            lly.clear();

                            memodb=mapmemodb.getWritableDatabase();

                            for (int i = 0; i < memo_title.size(); i++) {

                                String sql3;


                                sql3 = String.format("INSERT INTO mapmemotb VALUES(" + mapnum + ", " + i + ",'" + memo_title.get(i) + "', '" + memo_memo.get(i) +"' ,'"+ memo_check.get(i)+"' ,'"+ memo_Time.get(i)+"' ,'"+memo_bitmap.get(i)+"' ,"+
                                        memo_x.get(i)+" ,"+memo_y.get(i)+");");
                                memodb.execSQL(sql3);

                            }
                            memo_title.clear();
                            memo_memo.clear();
                            memo_check.clear();
                            memo_Time.clear();
                            memo_bitmap.clear();
                            memo_x.clear();
                            memo_y.clear();
                            namedb.close();
                            mapdb.close();
                            memodb.close();

                            Toast.makeText(getApplicationContext(), R.string.tr_savecom, Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            finish();
                        }
                    });

                    ad.setNeutralButton(R.string.tr_memo_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    ad.show();


                }else if(llx.size()==0){
                    Toast.makeText(getApplicationContext(),R.string.tr_saveno,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),R.string.tr_saveno2,Toast.LENGTH_SHORT).show();
                }

            }

        });
        btn_startend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(toggle==0) {//gps 기능 사용시작
                        if (Build.VERSION.SDK_INT >= 23 &&
                                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(travel.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    0);
                        } else {

                            toggle=1;
                            btn_startend.setText(R.string.tr_stop);

                            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                    3000,
                                    5,
                                    gpsLocationListener);
                            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                    3000,
                                    5,
                                    gpsLocationListener);
                        }
                    }
                    else if(toggle==1) {
                        toggle=2;
                        Toast.makeText(getApplicationContext(),R.string.tr_restopcontent,Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),R.string.tr_stopcontent,Toast.LENGTH_SHORT).show();
                        toggle=0;
                        lm.removeUpdates(gpsLocationListener);
                        btn_startend.setText(R.string.tr_start);

                    }
                }catch(SecurityException e){
                }
            }
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {

                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                img_bitmap = BitmapToString(scaled);
                dlog_iv.setImageBitmap(scaled);

            }


        } catch (Exception e) {
            Toast.makeText(this, "Oops! 로딩에 오류가 있습니다.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }



    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude();
            latitude = location.getLatitude();

            LatLng point = new LatLng(latitude, longitude);

            mMap.clear();
            lly.add(latitude);
            llx.add(longitude);

            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            arrayPoints.add(point);
            polylineOptions.addAll(arrayPoints);
            mMap.addPolyline(polylineOptions);

            MarkerOptions markerOptions = new MarkerOptions();
            BitmapDrawable bitmapdraw2;
            bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.favoritemark);


            Bitmap b=bitmapdraw2.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            if(memo_x.size()!=0){
                for(int i=0;i<memo_x.size();i++) {
                    LatLng mark = new LatLng(memo_y.get(i), memo_x.get(i));
                    markerOptions.position(mark).title(memo_title.get(i));
                    mMap.addMarker(markerOptions);
                }
            }
            MarkerOptions mOptions2 = new MarkerOptions();
            mOptions2.position(point);
            mOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mOptions2.title(getString(R.string.t_markloca));
            mMap.addMarker(mOptions2);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));


        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private long time= 0;
    @Override
    public void onBackPressed(){
        if(dlog_layout.getVisibility()==View.VISIBLE){
            dlog_iv.setImageResource(R.drawable.ic_launcher_foreground);
            dlog_title.setText(null);
            dlog_memo.setText(null);
            img_bitmap=null;
            dlog_layout.setVisibility(View.GONE);
            noclicklayout.setVisibility(View.GONE);
            memo_select.check(R.id.priority1);
            inser=0;
            insermode=0;
        }else {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), R.string.tr_close, Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.getTitle().equals(getString(R.string.t_markloca))){

        }else {
            final CharSequence[] items = {getString(R.string.tri_insert), getString(R.string.tri_delete), getString(R.string.tr_memo_cancel)};
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            alertDialogBuilder.setItems(items,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int id) {

                            if (id == 0) {//수정
                                for (int i = 0; i < memo_x.size(); i++) {
                                    if (memo_x.get(i) == marker.getPosition().longitude && memo_y.get(i) == marker.getPosition().latitude) {

                                        if(memo_check.get(i).equals("관광지")||memo_check.get(i).equals("Tourist spot")) {
                                            memo_select.check(R.id.priority1);
                                        }else if(memo_check.get(i).equals("맛집")||memo_check.get(i).equals("Restaurant")) {
                                            memo_select.check(R.id.priority2);
                                        }else if(memo_check.get(i).equals("인물")||memo_check.get(i).equals("Person")) {
                                            memo_select.check(R.id.priority3);
                                        }else {
                                            memo_select.check(R.id.priority4);
                                        }
                                        img_bitmap=memo_bitmap.get(i);
                                        dlog_title.setText(memo_title.get(i));
                                        dlog_memo.setText(memo_memo.get(i));
                                        dlog_iv.setImageBitmap(StringToBitmap(memo_bitmap.get(i)));
                                        inser=i;
                                        insermarker = marker;
                                        insermode=1;
                                        inserx=memo_x.get(i);
                                        insery=memo_y.get(i);
                                        dlog_layout.setVisibility(View.VISIBLE);
                                        noclicklayout.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else if (id == 1) {
                                for (int i = 0; i < memo_x.size(); i++) {
                                    if (memo_x.get(i) == marker.getPosition().longitude && memo_y.get(i) == marker.getPosition().latitude) {
                                        memo_title.remove(i);
                                        memo_memo.remove(i);
                                        memo_Time.remove(i);
                                        memo_bitmap.remove(i);
                                        memo_x.remove(i);
                                        memo_y.remove(i);
                                        marker.remove();
                                    }
                                }
                            } else {

                            }

                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();


                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
