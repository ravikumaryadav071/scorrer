package com.scorrer.ravi.scorrer;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CricketScoreActivity extends AppCompatActivity {

    private Handler mainUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricket_score);

        mainUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                finishInitializing(msg.getData().getString("msg"));
            }
        };

        new TeamsDataEntry(this, "CRICKET", mainUIHandler);
        //FileOutputStream fos = openFileOutput("");

    }

    private void finishInitializing(String matchId){
        CustomViewPager tabs = (CustomViewPager) findViewById(R.id.tabs);
        if(tabs!=null){
            tabs.setAdapter(new TabAdapter(getSupportFragmentManager()));
            PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
            tabStrip.setViewPager(tabs);
            tabs.setPagingEnabled(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(event.getRepeatCount()>0){
                notifyBackPressed();
            }else{
                Toast.makeText(this, "Tap and hold back key to exit.", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void notifyBackPressed(){

        Intent i = new Intent(this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
