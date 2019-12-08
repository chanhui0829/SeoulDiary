package com.example.travel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Home Home = new Home();
    private MyMapList MyMapList = new MyMapList();
    private SearchMapActivity SearchMapActivity = new SearchMapActivity();
    private RecommendTour RecommendTour = new RecommendTour();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, Home).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());



    }
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();


            switch(menuItem.getItemId())
            {
                case R.id.menuitem_home:
                    transaction.replace(R.id.frameLayout, Home).commitAllowingStateLoss();
                    break;

                case R.id.menuitem_main:
                    transaction.replace(R.id.frameLayout, MyMapList).commitAllowingStateLoss();

                    break;

                case R.id.menuitem_SearchMapActivity:
                    transaction.replace(R.id.frameLayout, SearchMapActivity).commitAllowingStateLoss();
                    break;

                case R.id.menuitem_recommendtour:
                    transaction.replace(R.id.frameLayout, RecommendTour).commitAllowingStateLoss();
                    break;


            }

            return true;
        }
    }




    private long time= 0;
    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "뒤로가기를 한번 더 누르면 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {
            finish();
        }
    }
}