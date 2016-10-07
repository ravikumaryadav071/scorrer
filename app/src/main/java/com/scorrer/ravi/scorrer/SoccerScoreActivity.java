package com.scorrer.ravi.scorrer;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

public class SoccerScoreActivity extends AppCompatActivity {

    private Handler mainUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_score);

        mainUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                finishInitializing(msg.getData().getString("msg"));
            }
        };

        new TeamsDataEntry(this, "SOCCER", mainUIHandler);
        //FileOutputStream fos = openFileOutput("");

    }

    private void finishInitializing(String matchId){
        CustomViewPager tabs = (CustomViewPager) findViewById(R.id.tabs);
        if(tabs!=null){
            tabs.setOffscreenPageLimit(2);
            tabs.setAdapter(new SoccerTabAdapter(getSupportFragmentManager()));
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
