package com.scorrer.ravi.scorrer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

public class SoccerScoreCardFragment extends Fragment {

    private static final String ARG = "PAGE_NO";
    private int page_no;
    private static ArrayList<String> players = SoccerTeamFragment.players;
    private static ArrayList<String> Oplayers = SoccerTeamFragment.Oplayers;
    public static ArrayList<String> onField = new ArrayList<String>();
    public static ArrayList<String> OonField = new ArrayList<String>();
    private static PlayerListAdapter fadapter;
    private static PlayerListAdapter sadapter;
    private HighLightAdapter highlightAdapter;
    private StatsAdapter statsAdapter;
    private ArrayList<String> highlightType = new ArrayList<String>();
    private ArrayList<String> highlightText = new ArrayList<String>();
    private ArrayList<String> highlightTime = new ArrayList<String>();
    private String commentType = "";
    private String commentText = "";
    private String commentTime = "";
    private static ArrayList<HashMap<String, Integer>> teamStats = new ArrayList<HashMap<String, Integer>>();
    private static HashMap<String, HashMap<String, Integer>> ftPlayersStats = new HashMap<String, HashMap<String, Integer>>();
    private static HashMap<String, HashMap<String, Integer>> stPlayersStats = new HashMap<String, HashMap<String, Integer>>();
    private static HashMap<String, View> ftPlayersGroundView = new HashMap<String, View>();
    private static HashMap<String, View> stPlayersGroundView = new HashMap<String, View>();
    private ArrayList<String> nextPlayer = new ArrayList<String>();
    private static String halfTime;
    private static String fullTime;
    private static boolean started = false;
    private static int teamSize = 0;
    private static int possession;
    private int half = 1;
    private View movedView = null;
    private static TextView time;
    private String extraTime = "00:00";
    private static boolean finished = false;
    public static SoccerScoreCardFragment newInstance(int page_no){
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, page_no);
        SoccerScoreCardFragment fragment = new SoccerScoreCardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_no = getArguments().getInt(ARG);
        if(teamStats.size()==0) {
            HashMap<String, Integer> data = new HashMap<String, Integer>();
            data.put("possess", 0);
            data.put("goals", 0);
            data.put("corners", 0);
            data.put("off_sides", 0);
            data.put("fouls", 0);
            data.put("red_cards", 0);
            data.put("yellow_cards", 0);
            teamStats.add(data);
            data = new HashMap<String, Integer>();
            data.put("possess", 0);
            data.put("goals", 0);
            data.put("corners", 0);
            data.put("off_sides", 0);
            data.put("fouls", 0);
            data.put("red_cards", 0);
            data.put("yellow_cards", 0);
            teamStats.add(data);
            for (int i=0; i<SoccerTeamFragment.players.size(); i++) {
                data = new HashMap<String, Integer>();
                data.put("goals", 0);
                data.put("assists", 0);
                data.put("off_sides", 0);
                data.put("fouls", 0);
                data.put("red_cards", 0);
                data.put("yellow_cards", 0);
                ftPlayersStats.put(SoccerTeamFragment.players.get(i), data);
            }
            for (int i=0; i<SoccerTeamFragment.Oplayers.size(); i++) {
                data = new HashMap<String, Integer>();
                data.put("goals", 0);
                data.put("assists", 0);
                data.put("off_sides", 0);
                data.put("fouls", 0);
                data.put("red_cards", 0);
                data.put("yellow_cards", 0);
                stPlayersStats.put(SoccerTeamFragment.Oplayers.get(i), data);
            }
            float temp = Float.valueOf(SoccerTeamFragment.time)/(float) 2;
            int m = (int) Math.floor(temp);
            int s = (int) ((temp-(float) m)*60);
            String sm = String.valueOf(m);
            String ss = String.valueOf(s);
            if(sm.length()==1){
                sm = "0"+sm;
            }
            if(ss.length()==1){
                ss = "0"+ss;
            }
            halfTime = sm+":"+ss;
            fullTime = SoccerTeamFragment.time+":00";
            if (SoccerTeamFragment.toss.equals("F")) {
                possession = 1;
            } else {
                possession = 2;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        switch (page_no){
            case 1:
                view = inflater.inflate(R.layout.soccer_score_card, null);
                highlightAdapter = new HighLightAdapter(highlightType, highlightText, highlightTime);
                statsAdapter = new StatsAdapter(teamStats);

                ListView statsList = (ListView) view.findViewById(R.id.statistics);
                statsList.setAdapter(statsAdapter);
                ListView highlightList = (ListView) view.findViewById(R.id.highlights);
                highlightList.setAdapter(highlightAdapter);

                time = (TextView) view.findViewById(R.id.time);
                TextView ftn = (TextView) view.findViewById(R.id.first_team_name);
                ftn.setText(SoccerTeamFragment.team);
                TextView stn = (TextView) view.findViewById(R.id.second_team_name);
                stn.setText(SoccerTeamFragment.Oteam);

                final TextView ftnp = (TextView) view.findViewById(R.id.first_team_name_poss);
                ftnp.setText(SoccerTeamFragment.team);
                final TextView stnp = (TextView) view.findViewById(R.id.second_team_name_poss);
                stnp.setText(SoccerTeamFragment.Oteam);

                final TextView ftp = (TextView) view.findViewById(R.id.first_team_poss);
                final TextView stp = (TextView) view.findViewById(R.id.second_team_poss);
                final TextView extraTimetv = (TextView) view.findViewById(R.id.extra_time);
                final Button start = (Button) view.findViewById(R.id.start_pause);
                final Button goal = (Button) view.findViewById(R.id.goal);
                final TextView ftg = (TextView) view.findViewById(R.id.first_team_goals);
                final TextView stg = (TextView) view.findViewById(R.id.second_team_goals);

                Button penalty = (Button) view.findViewById(R.id.penalty);
                Button corner = (Button) view.findViewById(R.id.corner);
                Button foul = (Button) view.findViewById(R.id.foul);
                Button redCard = (Button) view.findViewById(R.id.red_card);
                Button yellowCard = (Button) view.findViewById(R.id.yellow_card);
                Button offSide = (Button) view.findViewById(R.id.off_side);
                Button throwIn = (Button) view.findViewById(R.id.throw_in);
                Button ownGoal = (Button) view.findViewById(R.id.own_goal);

                if(possession==1){
                    stp.animate().translationX(-stp.getWidth());
                    stp.setVisibility(View.INVISIBLE);
                }else{
                    ftp.animate().translationX(ftp.getWidth());
                    ftp.setVisibility(View.INVISIBLE);
                }

                ftnp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(possession==2){
                            possession = 1;
                            ftp.setVisibility(View.VISIBLE);
                            ftp.animate().translationX(0);
                            stp.setVisibility(View.VISIBLE);
                            stp.animate().translationX(-stp.getWidth());
                        }
                    }
                });

                stnp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(possession==1){
                            possession = 2;
                            stp.setVisibility(View.VISIBLE);
                            stp.animate().translationX(0);
                            ftp.setVisibility(View.VISIBLE);
                            ftp.animate().translationX(ftp.getWidth());
                        }
                    }
                });

                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        if(msg.getData().containsKey("extraTime")){
                           extraTimetv.setText(msg.getData().getString("extraTime"));
                        }
                        if(msg.getData().containsKey("startExtra")){
                            start.callOnClick();
                            extraTimetv.setText("00:00");
                            final EditText input = new EditText(getContext());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Extra Time")
                                    .setMessage("Time")
                                    .setIcon(R.drawable.watch)
                                    .setCancelable(false)
                                    .setView(input)
                                    .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(!input.getText().toString().trim().equals("")){
                                                extraTime = input.getText().toString().trim();
                                                if(extraTime.length()==1){
                                                    extraTime = "0"+extraTime+":00";
                                                }
                                                start.callOnClick();
                                                dialog.cancel();
                                            }
                                        }
                                    }).show();
                        }
                        if(msg.getData().containsKey("endExtra")){
                            start.callOnClick();
                            extraTimetv.setText("");
                            if(half==1){
                                commentType = "HT";
                                commentText = "Half Time.";
                                commentTime = time.getText().toString();
                                highlightTime.add(0, commentTime);
                                highlightText.add(0,commentText);
                                highlightType.add(0, commentType);
                                highlightAdapter.notifyAdapter(highlightType, highlightText, highlightTime);
                                commentTime = "";
                                commentText = "";
                                commentType = "";
                                half=2;
                            }else if(half==2){
                                start.setEnabled(false);
                                finished = true;
                            }
                        }
                        if(msg.getData().containsKey("time")){
                            time.setText(msg.getData().getString("time"));
                        }
                    }
                };

                start.setOnClickListener(new View.OnClickListener() {
                    Timer timer;
                    @Override
                    public void onClick(View v) {
                        if(onField.size()>0 && OonField.size()>0) {
                            if(teamSize==0){
                                if(onField.size()==OonField.size()){
                                    teamSize = onField.size();
                                }
                            }
                            if(teamSize!=0) {
                                if (!started) {
                                    start.setText("PAUSE");
                                    started = true;
                                    timer = new Timer();
                                    timer.scheduleAtFixedRate(new TimerTask(){
                                        @Override
                                        public void run() {
                                            String sTemp = time.getText().toString();
                                            String temp[] = sTemp.split(":");
                                            String sxTemp = extraTimetv.getText().toString();
                                            if (sTemp.equals(halfTime) || sTemp.equals(fullTime)) {
                                                if(!extraTime.equals("00:00")) {
                                                    temp = sxTemp.split(":");
                                                }
                                            }
                                            int m = Integer.valueOf(temp[0]);
                                            int s = Integer.valueOf(temp[1]);
                                            s++;
                                            if(s==60){
                                                s=0;
                                                m++;
                                            }
                                            String sm = String.valueOf(m);
                                            String ss = String.valueOf(s);
                                            if(sm.length()==1){
                                                sm = "0"+sm;
                                            }
                                            if(ss.length()==1){
                                                ss = "0"+ss;
                                            }
                                            String stime = sm+":"+ss;
                                            Message msg = handler.obtainMessage();
                                            Bundle b = new Bundle();

                                            if(sTemp.equals(halfTime) || sTemp.equals(fullTime)){
                                                if(!extraTime.equals("00:00")) {
                                                    if (stime.equals(extraTime)) {
                                                        b.putString("endExtra", "extraTime");
                                                        b.putString("extraTime", stime);
                                                    } else {
                                                        b.putString("extraTime", stime);
                                                    }
                                                }
                                            }

                                            if((stime.equals(halfTime)||stime.equals(fullTime)) && extraTime.equals("00:00")){
                                                b.putString("startExtra", "extraTime");
                                                b.putString("time", stime);
                                            }else{
                                                b.putString("time", stime);
                                            }
                                            msg.setData(b);
                                            handler.sendMessage(msg);
                                        }
                                    }, 0, 1000);
                                } else {
                                    start.setText("START");
                                    started = false;
                                    timer.cancel();
                                    timer = null;
                                }
                            }else{
                                Toast.makeText(getContext(), "Both teams must have equal players on field.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Place players on field.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                goal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int g = teamStats.get(0).get("goals");
                                g++;
                                teamStats.get(0).remove("goals");
                                teamStats.get(0).put("goals", g);
                                ftg.setText(String.valueOf(g));
                                getPlayer("GOAL", possession);
                                stnp.callOnClick();
                            }else{
                                int g = teamStats.get(1).get("goals");
                                g++;
                                teamStats.get(1).remove("goals");
                                teamStats.get(1).put("goals", g);
                                stg.setText(String.valueOf(g));
                                getPlayer("GOAL", possession);
                                ftnp.callOnClick();
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                corner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int c = teamStats.get(0).get("corners");
                                c++;
                                teamStats.get(0).remove("corners");
                                teamStats.get(0).put("corners", c);
                                getPlayer("CORNER", possession);
                            }else{
                                int c = teamStats.get(1).get("corners");
                                c++;
                                teamStats.get(1).remove("corners");
                                teamStats.get(1).put("corners", c);
                                getPlayer("CORNER", possession);
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                penalty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                getPlayer("PENALTY", possession);
                            }else{
                                getPlayer("PENALTY", possession);
                            }
                        }
                    }
                });

                foul.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int f = teamStats.get(0).get("fouls");
                                f++;
                                teamStats.get(0).remove("fouls");
                                teamStats.get(0).put("fouls", f);
                                getPlayer("FOUL", possession);
                                stnp.callOnClick();
                            }else{
                                int f = teamStats.get(1).get("fouls");
                                f++;
                                teamStats.get(1).remove("fouls");
                                teamStats.get(1).put("fouls", f);
                                getPlayer("FOUL", possession);
                                ftnp.callOnClick();
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                ownGoal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int g = teamStats.get(1).get("goals");
                                g++;
                                teamStats.get(1).remove("goals");
                                teamStats.get(1).put("goals", g);
                                stg.setText(String.valueOf(g));
                                getPlayer("OWN GOAL", possession);
                            }else{
                                int g = teamStats.get(0).get("goals");
                                g++;
                                teamStats.get(0).remove("goals");
                                teamStats.get(0).put("goals", g);
                                ftg.setText(String.valueOf(g));
                                getPlayer("OWN GOAL", possession);
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                redCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int r = teamStats.get(0).get("red_cards");
                                r++;
                                teamStats.get(0).remove("red_cards");
                                teamStats.get(0).put("red_cards", r);
                                getPlayer("RED CARD", possession);
                                stnp.callOnClick();
                            }else{
                                int r = teamStats.get(1).get("red_cards");
                                r++;
                                teamStats.get(1).remove("red_cards");
                                teamStats.get(1).put("red_cards", r);
                                getPlayer("RED CARD", possession);
                                ftnp.callOnClick();
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                yellowCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int y = teamStats.get(0).get("yellow_cards");
                                y++;
                                teamStats.get(0).remove("yellow_cards");
                                teamStats.get(0).put("yellow_cards", y);
                                getPlayer("YELLOW CARD", possession);
                                stnp.callOnClick();
                            }else{
                                int y = teamStats.get(1).get("yellow_cards");
                                y++;
                                teamStats.get(1).remove("yellow_cards");
                                teamStats.get(1).put("yellow_cards", y);
                                getPlayer("YELLOW CARD", possession);
                                ftnp.callOnClick();
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                offSide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(started){
                            commentTime = time.getText().toString();
                            if(possession==1){
                                int o = teamStats.get(0).get("off_sides");
                                o++;
                                teamStats.get(0).remove("off_sides");
                                teamStats.get(0).put("off_sides", o);
                                getPlayer("OFF SIDE", possession);
                                stnp.callOnClick();
                            }else{
                                int o = teamStats.get(1).get("off_sides");
                                o++;
                                teamStats.get(1).remove("off_sides");
                                teamStats.get(1).put("off_sides", o);
                                getPlayer("OFF SIDE", possession);
                                ftnp.callOnClick();
                            }
                            statsAdapter.notifyAdapter(teamStats);
                        }
                    }
                });

                throwIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentTime = time.getText().toString();
                        commentText = "Ball has gone out of play. Throw in.";
                        commentType = "T";
                        highlightTime.add(0, commentTime);
                        highlightText.add(0,commentText);
                        highlightType.add(0, commentType);
                        highlightAdapter.notifyAdapter(highlightType, highlightText, highlightTime);
                        commentTime = "";
                        commentText = "";
                        commentType = "";
                    }
                });
                break;
            case 2:
                view = inflater.inflate(R.layout.first_team_formation, null);

                TextView tn1 = (TextView) view.findViewById(R.id.team_name);
                tn1.setText(SoccerTeamFragment.team+"'s Formation");

                ListView ftList = (ListView) view.findViewById(R.id.players_list);
                fadapter = new PlayerListAdapter(players, 1);
                ftList.setAdapter(fadapter);
                ftList.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!players.contains(item.getText())){
                                    ViewGroup parent = (ViewGroup) movedView.getParent();
                                    parent.removeView(movedView);
                                    movedView = null;
                                    players.add(0, (String) item.getText());
                                    onField.remove(item.getText());
                                    fadapter.notifyAdapter(players);
                                    ftPlayersGroundView.remove(item.getText().toString());
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                final RelativeLayout fbp = (RelativeLayout) view.findViewById(R.id.football_pitch);
                fbp.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                final ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!onField.contains(item.getText())){
                                    onField.add((String) item.getText());
                                    players.remove(item.getText());
                                    fadapter.notifyAdapter(players);

                                    LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View playerOnGround = inflater1.inflate(R.layout.player_on_ground, null);
                                    TextView tv = (TextView) playerOnGround.findViewById(R.id.player_username);
                                    tv.setText(item.getText());
                                    playerOnGround.setX(event.getX());
                                    playerOnGround.setY(event.getY());

                                    int yellow = ftPlayersStats.get(item.getText()).get("yellow_cards");
                                    if(yellow!=0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                tv.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg, getActivity().getTheme()));
                                            }else{
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    tv.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                                                }
                                            }
                                        }else{
                                            tv.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                                        }
                                    }

                                    fbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            if(!finished) {
                                                movedView = v;
                                                movedView.setVisibility(View.GONE);
                                                ClipData.Item data = new ClipData.Item(item.getText());
                                                ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                                ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                                v.startDrag(dragData, sb, null, 0);
                                            }
                                            return true;
                                        }
                                    });
                                    playerOnGround.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            final int action = event.getAction();
                                            switch (action){
                                                case DragEvent.ACTION_DRAG_STARTED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENDED:
                                                    if(movedView!=null){
                                                        movedView.setVisibility(View.VISIBLE);
                                                        movedView = null;
                                                    }
                                                    return true;
                                            }
                                            return false;
                                        }
                                    });
                                    ftPlayersGroundView.put(item.getText().toString(), playerOnGround);
                                }else{
                                    movedView.setX(event.getX());
                                    movedView.setY(event.getY());
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                break;
            case 3:
                view = inflater.inflate(R.layout.second_team_formation, null);

                TextView tn2 = (TextView) view.findViewById(R.id.team_name);
                tn2.setText(SoccerTeamFragment.Oteam+"'s Formation");

                ListView stList = (ListView) view.findViewById(R.id.players_list);
                sadapter = new PlayerListAdapter(Oplayers, 2);
                stList.setAdapter(sadapter);

                stList.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!Oplayers.contains(item.getText())){
                                    ViewGroup parent = (ViewGroup) movedView.getParent();
                                    parent.removeView(movedView);
                                    movedView = null;
                                    Oplayers.add(0, (String) item.getText());
                                    OonField.remove(item.getText());
                                    sadapter.notifyAdapter(Oplayers);
                                    stPlayersGroundView.remove(item.getText().toString());
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });

                final RelativeLayout sbp = (RelativeLayout) view.findViewById(R.id.football_pitch);
                sbp.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action){
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;
                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;
                            case DragEvent.ACTION_DROP:
                                final ClipData.Item item = event.getClipData().getItemAt(0);
                                if(!OonField.contains(item.getText())){
                                    OonField.add((String) item.getText());
                                    Oplayers.remove(item.getText());
                                    sadapter.notifyAdapter(Oplayers);

                                    LayoutInflater inflater1 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View playerOnGround = inflater1.inflate(R.layout.player_on_ground, null);
                                    TextView tv = (TextView) playerOnGround.findViewById(R.id.player_username);
                                    tv.setText(item.getText());
                                    playerOnGround.setX(event.getX());
                                    playerOnGround.setY(event.getY());

                                    int yellow = stPlayersStats.get(item.getText()).get("yellow_cards");
                                    if(yellow!=0){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                tv.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg, getActivity().getTheme()));
                                            }else{
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    tv.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                                                }
                                            }
                                        }else{
                                            tv.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                                        }
                                    }

                                    sbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            if(!finished) {
                                                movedView = v;
                                                movedView.setVisibility(View.GONE);
                                                ClipData.Item data = new ClipData.Item(item.getText());
                                                ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                                ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                                v.startDrag(dragData, sb, null, 0);
                                            }
                                            return true;
                                        }
                                    });
                                    playerOnGround.setOnDragListener(new View.OnDragListener() {
                                        @Override
                                        public boolean onDrag(View v, DragEvent event) {
                                            final int action = event.getAction();
                                            switch (action){
                                                case DragEvent.ACTION_DRAG_STARTED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENTERED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_EXITED:
                                                    return true;
                                                case DragEvent.ACTION_DRAG_ENDED:
                                                    if(movedView!=null){
                                                        movedView.setVisibility(View.VISIBLE);
                                                        movedView = null;
                                                    }
                                                    return true;
                                            }
                                            return false;
                                        }
                                    });
                                    stPlayersGroundView.put(item.getText().toString(), playerOnGround);
                                }else{
                                    movedView.setX(event.getX());
                                    movedView.setY(event.getY());
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                if(movedView!=null){
                                    movedView.setVisibility(View.VISIBLE);
                                    movedView = null;
                                }
                                return true;
                        }
                        return false;
                    }
                });
                break;
        }
        return view;
    }

    private void getPlayer(final String action, final int possess){

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        SelectPlayerAdapter Fadapter;
        if(action.equals("GOAL")){
            title.setText("GOAL SCORER");
        }else if(action.equals("OWN GOAL")){
            title.setText("OWN GOAL SCORER");
        }else if(action.equals("GOAL ASSISTANT")){
            title.setText("GOAL ASSISTANT");
        }else if(action.equals("OFF SIDE")){
            title.setText("OFF SIDE");
        }else if(action.equals("CORNER")){
            title.setText("CORNER TAKER");
        }else if(action.equals("RED CARD")){
            title.setText("RED CARD");
        }else if(action.equals("YELLOW CARD")){
            title.setText("YELLOW CARD");
        }else if(action.equals("FOUL")){
            title.setText("FOUL");
        }else if(action.equals("PENALTY")){
            title.setText("PENALTY TAKER");
        }
        ArrayList<String> lineup = new ArrayList<String>();
        if(possess==1){
            team_name.setText(SoccerTeamFragment.team);
            lineup.addAll(onField);
        }else{
            team_name.setText(SoccerTeamFragment.Oteam);
            lineup.addAll(OonField);
        }
        if(action.equals("GOAL ASSISTANT")){
            lineup.remove(nextPlayer.get(0));
        }
        Fadapter = new SelectPlayerAdapter(lineup);
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextPlayer.size()==1) {
                    alert.cancel();
                    String index = null;
                    if(action.equals("GOAL")){
                        index = "goals";
                    }else if(action.equals("GOAL ASSISTANT")){
                        index = "assists";
                    }else if(action.equals("OFF SIDE")){
                        index = "off_sides";
                    }else if(action.equals("RED CARD")){
                        index = "red_cards";
                    }else if(action.equals("YELLOW CARD")){
                        index = "yellow_cards";
                    }else if(action.equals("FOUL")){
                        index = "fouls";
                    }else {
                        if (action.equals("CORNER")) {
                            commentText = nextPlayer.get(0)+" takes the corner kick.";
                            commentType="C";
                        } else if (action.equals("PENALTY")) {
                            commentText = nextPlayer.get(0)+" takes the penalty kick.";
                            commentType="P";
                        } else if (action.equals("OWN GOAL")) {
                            commentText = nextPlayer.get(0)+" scores own goal.";
                            commentType="G";
                        }
                        highlightTime.add(0, commentTime);
                        highlightText.add(0,commentText);
                        highlightType.add(0, commentType);
                        highlightAdapter.notifyAdapter(highlightType, highlightText, highlightTime);
                        commentTime = "";
                        commentText = "";
                        commentType = "";
                        nextPlayer.clear();
                        return;
                    }
                    int value;
                    if(possess==1){
                        value = ftPlayersStats.get(nextPlayer.get(0)).get(index);
                        value++;
                        ftPlayersStats.get(nextPlayer.get(0)).remove(index);
                        ftPlayersStats.get(nextPlayer.get(0)).put(index, value);
                    }else{
                        Log.e(nextPlayer.get(0) , stPlayersStats.toString());
                        value = stPlayersStats.get(nextPlayer.get(0)).get(index);
                        value++;
                        stPlayersStats.get(nextPlayer.get(0)).remove(index);
                        stPlayersStats.get(nextPlayer.get(0)).put(index, value);
                    }

                    if(action.equals("GOAL")){
                        commentText = nextPlayer.get(0)+" has scored a goal for ";
                        if(possess==1){
                            commentText += SoccerTeamFragment.team;
                        }else{
                            commentText += SoccerTeamFragment.Oteam;
                        }
                        getPlayer("GOAL ASSISTANT", possess);
                    }else if(action.equals("GOAL ASSISTANT")){
                        commentText += " by assist of "+nextPlayer.get(0)+".";
                        commentType = "G";
                    }else if(action.equals("OFF SIDE")){
                        commentText = nextPlayer.get(0)+" found off side.";
                        commentType = "O";
                    }else if(action.equals("RED CARD")){
                        commentText = nextPlayer.get(0)+" booked for red card.";
                        commentType = "R";
                    }else if(action.equals("YELLOW CARD")){
                        if(value==1) {
                            commentText = nextPlayer.get(0) + " booked for yellow card.";
                            View v1;
                            if(possess==1){
                                v1 = ftPlayersGroundView.get(nextPlayer.get(0));
                            }else{
                                v1 = stPlayersGroundView.get(nextPlayer.get(0));
                            }
                            TextView pu = (TextView) v1.findViewById(R.id.player_username);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    pu.setBackground(getActivity().getResources().getDrawable(R.drawable.player_yellow_bg, getActivity().getTheme()));
                                }else{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        pu.setBackground(getActivity().getResources().getDrawable(R.drawable.player_yellow_bg));
                                    }
                                }
                            }else{
                                pu.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_yellow_bg));
                            }
                        }else{
                            commentText = "Second yellow makes it red for "+nextPlayer.get(0) + ".";
                        }
                        commentType = "Y";
                        if(possess==1){
                            int value1 = ftPlayersStats.get(nextPlayer.get(0)).get("red_cards");
                            value1++;
                            ftPlayersStats.get(nextPlayer.get(0)).remove("red_cards");
                            ftPlayersStats.get(nextPlayer.get(0)).put("red_cards", value1);
                        }else{
                            int value1 = stPlayersStats.get(nextPlayer.get(0)).get("red_cards");
                            value1++;
                            stPlayersStats.get(nextPlayer.get(0)).remove("red_cards");
                            stPlayersStats.get(nextPlayer.get(0)).put("red_cards", value1);
                        }
                    }else if(action.equals("FOUL")){
                        commentText = "Foul by "+nextPlayer.get(0)+".";
                        commentType = "F";
                    }

                    if(action.equals("RED CARD") || (action.equals("YELLOW CARD") && value==2)){
                        if(possess==1){
                            View v1 = ftPlayersGroundView.get(nextPlayer.get(0));
                            v1.setVisibility(View.GONE);
                            ftPlayersGroundView.remove(nextPlayer.get(0));
                            players.add(nextPlayer.get(0));
                            onField.remove(nextPlayer.get(0));
                            fadapter.notifyAdapter(players);
                        }else{
                            View v1 = stPlayersGroundView.get(nextPlayer.get(0));
                            v1.setVisibility(View.GONE);
                            stPlayersGroundView.remove(nextPlayer.get(0));
                            Oplayers.add(nextPlayer.get(0));
                            OonField.remove(nextPlayer.get(0));
                            sadapter.notifyAdapter(Oplayers);
                        }
                    }

                    if((action.equals("YELLOW CARD") && value==2)){
                        int value2;
                        if(possess==1){
                            value2 = teamStats.get(0).get("red_cards");
                            value2++;
                            teamStats.get(0).remove("red_cards");
                            teamStats.get(0).put("red_cards", value2);
                        }else{
                            value2 = teamStats.get(1).get("red_cards");
                            value2++;
                            teamStats.get(1).remove("red_cards");
                            teamStats.get(1).put("red_cards", value2);
                        }
                        statsAdapter.notifyAdapter(teamStats);
                    }

                    if(!action.equals("GOAL")) {
                        highlightTime.add(0, commentTime);
                        highlightText.add(0, commentText);
                        highlightType.add(0, commentType);
                        highlightAdapter.notifyAdapter(highlightType, highlightText, highlightTime);
                        commentTime = "";
                        commentText = "";
                        commentType = "";
                    }
                    nextPlayer.clear();
                }else{
                    if(action.equals("GOAL ASSISTANT")){
                        alert.cancel();
                        commentText += ".";
                        commentType = "G";
                        highlightTime.add(0, commentTime);
                        highlightText.add(0,commentText);
                        highlightType.add(0, commentType);
                        highlightAdapter.notifyAdapter(highlightType, highlightText, highlightTime);
                        commentTime = "";
                        commentText = "";
                        commentType = "";
                    }else {
                        Toast.makeText(getContext(), "Can select max 1 player.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    class StatsAdapter extends BaseAdapter{

        ArrayList<HashMap<String, Integer>> teamStats;
        ArrayList<String> stats = new ArrayList<String>();
        ArrayList<String> statsH = new ArrayList<String>();
        public StatsAdapter(ArrayList<HashMap<String, Integer>> teamStats) {
            this.teamStats = teamStats;
            stats.add("possess");
            stats.add("goals");
            stats.add("corners");
            stats.add("off_sides");
            stats.add("fouls");
            stats.add("yellow_cards");
            stats.add("red_cards");

            statsH.add("POSSESSION");
            statsH.add("GOALS");
            statsH.add("CORNERS");
            statsH.add("OFF SIDES");
            statsH.add("FOULS");
            statsH.add("YELLOW CARDS");
            statsH.add("RED CARDS");
        }

        @Override
        public int getCount() {
            return stats.size();
        }

        @Override
        public Object getItem(int position) {
            return stats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.soccer_stats, null);
            }
            else {
                view = convertView;
            }
            TextView statType = (TextView) view.findViewById(R.id.statsType);
            View ftsb = view.findViewById(R.id.ft_bar);
            View stsb = view.findViewById(R.id.st_bar);
            TextView fts = (TextView) view.findViewById(R.id.ft_stat);
            TextView sts = (TextView) view.findViewById(R.id.st_stat);
            statType.setText(statsH.get(position));
            int val1 = teamStats.get(0).get(stats.get(position));
            int val2 = teamStats.get(1).get(stats.get(position));
            fts.setText(String.valueOf(val1));
            sts.setText(String.valueOf(val2));
            if(val1!=0||val2!=0){
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ftsb.getLayoutParams();
                lp.weight = val1;
                ftsb.setLayoutParams(lp);
                lp = (LinearLayout.LayoutParams) stsb.getLayoutParams();
                lp.weight = val2;
                stsb.setLayoutParams(lp);
            }else{
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ftsb.getLayoutParams();
                lp.weight = 1;
                ftsb.setLayoutParams(lp);
                lp = (LinearLayout.LayoutParams) stsb.getLayoutParams();
                lp.weight = 1;
                stsb.setLayoutParams(lp);
            }

            return view;
        }

        public void notifyAdapter(ArrayList<HashMap<String, Integer>> teamStats) {
            this.teamStats = teamStats;
            notifyDataSetChanged();
        }
    }

    class SelectPlayerAdapter extends BaseAdapter{

        private ArrayList<String> players;

        public SelectPlayerAdapter(ArrayList<String> players){
            this.players = players;
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
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                    if(!nextPlayer.contains(plN.getText().toString())) {
                        nextPlayer.add(plN.getText().toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            v.setBackgroundColor(getResources().getColor(R.color.grayBG, null));
                        } else {
                            v.setBackgroundColor(getResources().getColor(R.color.grayBG));
                        }
                    }else{
                        nextPlayer.remove(plN.getText().toString());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            v.setBackgroundColor(getResources().getColor(R.color.colorWhite, null));
                        } else {
                            v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }
                    }
                }
            });
            return view;
        }
    }

    class HighLightAdapter extends BaseAdapter{

        ArrayList<String> highlightType;
        ArrayList<String> highlightText;
        ArrayList<String> highlightTime;

        public HighLightAdapter(ArrayList<String> highlightType, ArrayList<String> highlightText, ArrayList<String> highlightTime){
            this.highlightType = highlightType;
            this.highlightText = highlightText;
            this.highlightTime = highlightTime;
        }

        @Override
        public int getCount() {
            return highlightType.size();
        }

        @Override
        public Object getItem(int position) {
            return highlightText.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.soccer_highlights, null);
            }else{
                view = convertView;
            }
            TextView commentType = (TextView) view.findViewById(R.id.comment_type);
            TextView comment = (TextView) view.findViewById(R.id.comment);
            TextView commentTime = (TextView) view.findViewById(R.id.comment_time);
            commentType.setText(highlightType.get(position));
            comment.setText(highlightText.get(position));
            commentTime.setText(highlightTime.get(position));
            if(highlightType.get(position).equals("G")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_green_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_green_bg));
                        }
                    }
                }else{
                    commentType.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_green_bg));
                }
            }else if(highlightType.get(position).equals("Y")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_yellow_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_yellow_bg));
                        }
                    }
                }else{
                    commentType.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_yellow_bg));
                }
            }else if(highlightType.get(position).equals("R")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_red_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_red_bg));
                        }
                    }
                }else{
                    commentType.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_red_bg));
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            commentType.setBackground(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_bg));
                        }
                    }
                }else{
                    commentType.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.soccer_commentry_bg));
                }
            }
            return view;
        }

        public void notifyAdapter(ArrayList<String> highlightType, ArrayList<String> highlightText, ArrayList<String> highlightTime){
            this.highlightType = highlightType;
            this.highlightText = highlightText;
            this.highlightTime = highlightTime;
            notifyDataSetChanged();
        }

    }

    class PlayerListAdapter extends BaseAdapter {

        ArrayList<String> lineup;
        int team;

        public PlayerListAdapter(ArrayList<String> lineup, int team) {
            this.lineup = lineup;
            this.team = team;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return lineup.size();
        }

        @Override
        public Object getItem(int position) {
            return lineup.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            if(convertView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.player_team_formation_entry, null);
            }else{
                view = convertView;
            }
            TextView username = (TextView) view.findViewById(R.id.player_username);
            username.setText(lineup.get(position));
            int red = 0;
            int yellow = 0;
            if(team==1) {
                Log.e("data", ftPlayersStats.toString());
                red = ftPlayersStats.get(lineup.get(position)).get("red_cards");
                yellow = ftPlayersStats.get(lineup.get(position)).get("yellow_cards");
            }else{
                red = stPlayersStats.get(lineup.get(position)).get("red_cards");
                yellow = stPlayersStats.get(lineup.get(position)).get("yellow_cards");
            }
            if(red!=0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_red_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_red_bg));
                        }
                    }
                }else{
                    view.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_red_bg));
                }
            }else if(yellow!=0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                        }
                    }
                }else{
                    view.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_yellow_bg));
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_bg, getActivity().getTheme()));
                    }else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.setBackground(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_bg));
                        }
                    }
                }else{
                    view.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.player_team_formation_entry_bg));
                }
            }
            final View finalView = view;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!finished) {
                        int red = 0;
                        int pof = teamSize;
                        int cpof = 0;
                        if(team==1) {
                            red = ftPlayersStats.get(lineup.get(position)).get("red_cards");
                            pof -= teamStats.get(0).get("red_cards");
                            cpof = onField.size();
                        }else{
                            red = stPlayersStats.get(lineup.get(position)).get("red_cards");
                            pof -= teamStats.get(0).get("red_cards");
                            cpof = OonField.size();
                        }
                        if(red==0){
                            if(time.getText().toString().equals("00:00") || (cpof<pof) ) {
                                movedView = v;
                                movedView.setVisibility(View.GONE);
                                ClipData.Item data = new ClipData.Item(lineup.get(position));
                                ClipData dragData = new ClipData(lineup.get(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                ShadowBuilder sb = new ShadowBuilder(finalView, getContext());
                                v.startDrag(dragData, sb, null, 0);
                            }else{
                                Toast.makeText(getContext(), "To substitute firstly remove player from ground.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    return true;
                }
            });
            return view;
        }

        public void notifyAdapter(ArrayList<String> lineup){
            this.lineup = lineup;
            notifyDataSetChanged();
        }
    }

    static class ShadowBuilder extends View.DragShadowBuilder{
        private static Drawable shadow;
        private int width, height;
        private String drawText;
        public ShadowBuilder(View view, Context context) {
            super(view);
            TextView tv = (TextView) view.findViewById(R.id.player_username);
            drawText = (String) tv.getText();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                shadow = context.getResources().getDrawable(R.drawable.player_model, context.getTheme());
            }else{
                shadow = context.getResources().getDrawable(R.drawable.player_model);
            }
        }

        @Override
        public void onProvideShadowMetrics (Point size, Point touch) {
            width = 60;
            height = 80;
            shadow.setBounds(0, 0, width, height);
            size.set(width, height);
            touch.set(width/2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            shadow.draw(canvas);
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize((float) (20));
            int xPos = (canvas.getWidth() / 2);
            int yPos = (int) ((canvas.getHeight() / 3) - ((paint.descent() + paint.ascent())));
            canvas.drawText(drawText, xPos, yPos, paint);
        }

    }

}
