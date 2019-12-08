package com.example.travel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class RecommendTourDetail extends AppCompatActivity {
    ImageView detail_img;
    TextView txt_title,txt_content,txt_addr,txt_tel;
    Button btn_roadsearch, btn_map;
    String detail_url,keyword, contentid,keynum,language,mapx,mapy;
    String detail_content,detail_title,detail_addr,detail_tel;

    private SearchMapActivity SearchMapActivity = new SearchMapActivity();

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recommendtourdetail);

        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_content = (TextView) findViewById(R.id.txt_content);
        txt_addr = (TextView) findViewById(R.id.txt_addr);
        txt_tel = (TextView) findViewById(R.id.txt_tel);

        btn_roadsearch = (Button) findViewById(R.id.btn_roadsearch);
        detail_img = (ImageView) this.findViewById(R.id.detail_img);
        btn_map = (Button) findViewById(R.id.btn_map);

        txt_content.setMovementMethod(new ScrollingMovementMethod());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"));

        toolbar.setLogo(ContextCompat.getDrawable(getBaseContext(), R.drawable.homelogo));

        toolbar.setTitleTextColor(Color.parseColor("#D5D5D5"));
        toolbar.setTitleMarginTop(70);
        toolbar.setTitleMarginEnd(50);
        toolbar.setTitle("Recommend Tour");
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
        contentid = intent.getStringExtra("contentid");

        if(Home.languagetype.equals("0")){
            language = "KorService";        }
        else if(Home.languagetype.equals("1")){
            language = "EngService";
        }

        keynum = "vjv7CB%2FsAljrwMUUuRhv7F01yS6dNHpU1BU8haGNwp3Q%2FhKNMLb%2BZFD%2BzB9Nw0n94GRT1lk%2B0nvN%2Fq3G%2Btq38A%3D%3D";


        //264111
        detail_url = "http://api.visitkorea.or.kr/openapi/service/rest/"+language+"/detailCommon?" +
                "ServiceKey=" +keynum+ "&contentId=" + contentid + "&MobileOS=ETC&" +
                "MobileApp=appName&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y&_type=json";





        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(detail_url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            readStream(in);
                            urlConnection.disconnect();

                        }catch (Exception e){

                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }
                }

                catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(detail_url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        try {
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                            readStream(in);
                            urlConnection.disconnect();

                        }catch (Exception e){

                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "에러발생", Toast.LENGTH_SHORT).show();
                    }
                }

                catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();



        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapintent = new Intent(getApplicationContext(), RecommendMap.class);
                mapintent.putExtra("mapx",mapx);
                mapintent.putExtra("mapy",mapy);
                mapintent.putExtra("title",detail_title);
                mapintent.putExtra("addr",detail_addr);
                startActivity(mapintent);
            }
        });

        btn_roadsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roadsearch = new Intent(getApplicationContext(), RecommendSearchMapActivity.class);
                roadsearch.putExtra("addr",detail_addr);
                startActivity(roadsearch);
            }
        });

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

    public void readStream(InputStream in){
        String data = readData(in);
        try {
            JSONObject json = new JSONObject(data);
            JSONObject requestjson = json.getJSONObject("response");
            JSONObject bodyjson = requestjson.getJSONObject("body");
            JSONObject items = bodyjson.getJSONObject("items");
            JSONObject item = items.getJSONObject("item");



            String firstimage;
            if(item.isNull("firstimage")){
                firstimage = "R.drawable.image";
            }else{
                firstimage = item.getString("firstimage");
            }


            if (firstimage.equals("R.drawable.image")){
                detail_img.setImageResource(R.drawable.image);

            }else{
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(firstimage)
                        .into(new BitmapImageViewTarget(detail_img) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                //Play with bitmap
                                super.setResource(resource);
                            }
                        });
            }


            if(item.isNull("title")){
                detail_title = "";
            }
            else{
                detail_title = item.getString("title");
                detail_title = Html.fromHtml(detail_title).toString();

            }


            if(item.isNull("overview")){
                detail_content = "";
            }
            else {
                detail_content = item.getString("overview");
                detail_content = Html.fromHtml(detail_content).toString();
            }


            if(item.isNull("addr1")){
                detail_addr = "";
            }
            else {
                detail_addr = item.getString("addr1");
                detail_addr = Html.fromHtml(detail_addr).toString();

            }


            if(item.isNull("tel")){
                detail_tel = "";
            }
            else {
                detail_tel = item.getString("tel");
                detail_tel = Html.fromHtml(detail_tel).toString();
                txt_tel.setText("Tel)"+detail_tel);
            }

            mapx =item.getString("mapx");
            mapx =Html.fromHtml(mapx).toString();

            mapy =item.getString("mapy");
            mapy =Html.fromHtml(mapy).toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                txt_title.setText(detail_title);
                txt_addr.setText(detail_addr);

                txt_content.setText(detail_content);
            }
        });
    }

    public String readData(InputStream is){
        String data = "";
        Scanner s = new Scanner(is);
        while(s.hasNext()) data += s.nextLine() + "\n";
        s.close();
        return data;
    }
    Handler mHandler = new Handler();


}

