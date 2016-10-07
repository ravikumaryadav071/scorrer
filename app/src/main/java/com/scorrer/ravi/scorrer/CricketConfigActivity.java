package com.scorrer.ravi.scorrer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CricketConfigActivity extends AppCompatActivity implements CricketTeamFragment.FragmentCallBackListener {

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
        toolbar.setTitle("CRICKET");
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

        CricketTeamFragment.team = "";
        CricketTeamFragment.Oteam = "";
        CricketTeamFragment.players.clear();
        CricketTeamFragment.Oplayers.clear();

        CricketTeamFragment firstFragment = CricketTeamFragment.newInstance(1, null, nextButton);
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
            CricketTeamFragment fragment = CricketTeamFragment.newInstance(++pageNo, (String) data.get("team1_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==2){
            CricketTeamFragment fragment = CricketTeamFragment.newInstance(++pageNo, (String) data.get("team1_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==3){
            CricketTeamFragment fragment = CricketTeamFragment.newInstance(++pageNo, (String) data.get("team2_name"), nextButton);
            ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.popup_enter, R.anim.popup_exit, R.anim.popup_pop_enter, R.anim.popup_pop_exit);
            ft.replace(R.id.fragment_container, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.addToBackStack(null);
            ft.commit();
        }else if(pageNo==4){
            CricketTeamFragment fragment = CricketTeamFragment.newInstance(++pageNo, null, nextButton);
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

            firstTeamB.setText(CricketTeamFragment.team);
            secondTeamB.setText(CricketTeamFragment.Oteam);
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
                    CricketTeamFragment.toss = "F";
                    ad.cancel();
                    doRest();

                }
            });

            secondTeamB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CricketTeamFragment.toss = "S";
                    ad.cancel();
                    doRest();
                }
            });
        }
    }

    private void doRest(){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        final String batT;
        final String bolT;
        final PlayerListAdapter Fadapter;
        final PlayerListAdapter Sadapter;
        if(CricketTeamFragment.toss.equals("F")){
            batT = CricketTeamFragment.team;
            bolT = CricketTeamFragment.Oteam;
            Fadapter = new PlayerListAdapter(CricketTeamFragment.players, "BATSMANSELECTION");
            Sadapter = new PlayerListAdapter(CricketTeamFragment.Oplayers, "BOWLERSELECTION");
        }else{
            batT = CricketTeamFragment.Oteam;
            bolT = CricketTeamFragment.team;
            Fadapter = new PlayerListAdapter(CricketTeamFragment.Oplayers, "BATSMANSELECTION");
            Sadapter = new PlayerListAdapter(CricketTeamFragment.players, "BOWLERSELECTION");
        }
        title.setText("SELECT OPENERS");
        team_name.setText(batT);
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CricketConfigActivity", "2");
                LayoutInflater inflater = getLayoutInflater();
                if(CricketTeamFragment.currentBatsmen.size()==2){
                    //new ContextThemeWrapper(getApplicationContext(), R.style.AppTheme)
                    alert.cancel();
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(new ContextThemeWrapper(CricketConfigActivity.this, R.style.AppTheme));
                    View view = inflater.inflate(R.layout.choose_bowler, null);
                    TextView title = (TextView) view.findViewById(R.id.title);
                    TextView team_name = (TextView) view.findViewById(R.id.team_name);
                    ListView playersL = (ListView) view.findViewById(R.id.players_name);
                    Button proceed = (Button) view.findViewById(R.id.proceed);
                    title.setText("SELECT OPENING BOWLER");
                    team_name.setText(bolT);
                    playersL.setAdapter(Sadapter);
                    adBuilder.setCustomTitle(view);
                    final AlertDialog alert = adBuilder.create();
                    alert.show();
                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.e("CricketConfigActivity", "3");
                            if(CricketTeamFragment.currentBowlers.size()==1) {
                                alert.cancel();
                                Intent i = new Intent(getApplicationContext(), CricketScoreActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), "Select one bowler", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "Select two players", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    class PlayerListAdapter extends BaseAdapter{

        private ArrayList<String> players;
        private String action;

        public PlayerListAdapter(ArrayList<String> players, String action){
            this.players = players;
            this.action = action;
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.team_mates_sugg, null);
            }
            else {
                view = convertView;
            }
            final TextView plN = (TextView) view.findViewById(R.id.team_mate);
            plN.setText(players.get(position));
            plN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(action.equals("BATSMANSELECTION")){
                        if(!CricketTeamFragment.currentBatsmen.contains(plN.getText().toString())) {
                            CricketTeamFragment.currentBatsmen.add(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG));
                            }
                        }else{
                            CricketTeamFragment.currentBatsmen.remove(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            }
                        }
                    }else{
                        if(!CricketTeamFragment.currentBowlers.contains(plN.getText().toString())) {
                            CricketTeamFragment.currentBowlers.add(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG));
                            }
                        }else{
                            CricketTeamFragment.currentBowlers.remove(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            }
                        }
                    }
                }
            });
            return view;
        }
    }
}