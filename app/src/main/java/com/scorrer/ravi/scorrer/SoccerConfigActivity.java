package com.scorrer.ravi.scorrer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class SoccerConfigActivity extends AppCompatActivity implements SoccerTeamFragment.FragmentCallBackListener{

    private Fragment fragment;
    private FragmentTransaction ft;
    private RelativeLayout fragmentContainer;
    private Button nextButton;
    private HashMap<String, Object> data = new HashMap<String, Object>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("SOCCER");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        nextButton = (Button) findViewById(R.id.next_slide);

        SoccerTeamFragment.team = "";
        SoccerTeamFragment.Oteam = "";
        SoccerTeamFragment.players.clear();
        SoccerTeamFragment.Oplayers.clear();

        SoccerTeamFragment firstFragment = SoccerTeamFragment.newInstance(1, null, nextButton);
        ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, firstFragment);
        ft.commit();
    }

    @Override
    public void onCallBack(HashMap<String, Object> msg, int pageNo) {
        for(Map.Entry<String, Object> param: msg.entrySet()){
            data.put(param.getKey(), param.getValue());
        }
        if(pageNo==1){
            SoccerTeamFragment fragment = SoccerTeamFragment.newInstance(++pageNo, (String) data.get("team1_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==2){
            SoccerTeamFragment fragment = SoccerTeamFragment.newInstance(++pageNo, (String) data.get("team1_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==3){
            SoccerTeamFragment fragment = SoccerTeamFragment.newInstance(++pageNo, (String) data.get("team2_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==4){
            SoccerTeamFragment fragment = SoccerTeamFragment.newInstance(++pageNo, null, nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==5){
            final AlertDialog ad;

            Button firstTeamB = new Button(this);
            Button secondTeamB = new Button(this);
            LinearLayout ll = new LinearLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            firstTeamB.setLayoutParams(lp);
            secondTeamB.setLayoutParams(lp);

            firstTeamB.setText(SoccerTeamFragment.team);
            secondTeamB.setText(SoccerTeamFragment.Oteam);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            ll.setGravity(Gravity.CENTER);
            ll.addView(firstTeamB);
            ll.addView(secondTeamB);
            ad = new AlertDialog.Builder(this)
                    .setTitle("Toss")
                    .setIcon(R.drawable.coin_toss_icon)
                    .setView(ll)
                    .setCancelable(false).show();

            firstTeamB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoccerTeamFragment.toss = "F";
                    ad.cancel();
                    doRest();

                }
            });

            secondTeamB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoccerTeamFragment.toss = "S";
                    ad.cancel();
                    doRest();
                }
            });
        }
    }

    private void doRest(){
        Intent i = new Intent(getApplicationContext(), SoccerScoreActivity.class);
        startActivity(i);
        finish();
    }
}
