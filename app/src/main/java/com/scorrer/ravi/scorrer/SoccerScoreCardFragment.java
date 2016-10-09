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
    private ArrayList<String> highlightType = new ArrayList<String>();
    private ArrayList<String> highlightText = new ArrayList<String>();;
    private ArrayList<String> highlightTime = new ArrayList<String>();;
    private static ArrayList<HashMap<String, Integer>> teamStats = new ArrayList<HashMap<String, Integer>>();
    private static HashMap<String, HashMap<String, Integer>> ftPlayersStats = new HashMap<String, HashMap<String, Integer>>();
    private static HashMap<String, HashMap<String, Integer>> stPlayersStats = new HashMap<String, HashMap<String, Integer>>();
    private static HashMap<String, View> ftPlayersGroundView = new HashMap<String, View>();
    private static HashMap<String, View> stPlayersGroundView = new HashMap<String, View>();
    private static String halfTime;
    private static String fullTime;
    private static boolean started = false;
    private static int teamSize = 0;
    private static int possession;
    private int half = 1;
    private View movedView = null;
    private TextView time;
    private String extraTime = "00:00";

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
            data = new HashMap<String, Integer>();
            data.put("goals", 0);
            data = new HashMap<String, Integer>();
            data.put("corners", 0);
            data = new HashMap<String, Integer>();
            data.put("off_sides", 0);
            data = new HashMap<String, Integer>();
            data.put("fouls", 0);
            data = new HashMap<String, Integer>();
            data.put("red_cards", 0);
            data = new HashMap<String, Integer>();
            data.put("yellow_cards", 0);
            teamStats.add(data);
            data = new HashMap<String, Integer>();
            data.put("possess", 0);
            data = new HashMap<String, Integer>();
            data.put("goals", 0);
            data = new HashMap<String, Integer>();
            data.put("corners", 0);
            data = new HashMap<String, Integer>();
            data.put("off_sides", 0);
            data = new HashMap<String, Integer>();
            data.put("fouls", 0);
            data = new HashMap<String, Integer>();
            data.put("red_cards", 0);
            data = new HashMap<String, Integer>();
            data.put("yellow_cards", 0);
            teamStats.add(data);
            for (int i=0; i<SoccerTeamFragment.players.size(); i++) {
                data = new HashMap<String, Integer>();
                data.put("goals", 0);
                data = new HashMap<String, Integer>();
                data.put("assists", 0);
                data = new HashMap<String, Integer>();
                data.put("off_sides", 0);
                data = new HashMap<String, Integer>();
                data.put("fouls", 0);
                data = new HashMap<String, Integer>();
                data.put("red_cards", 0);
                data = new HashMap<String, Integer>();
                data.put("yellow_cards", 0);
                ftPlayersStats.put(SoccerTeamFragment.players.get(i), data);
            }
            for (int i=0; i<SoccerTeamFragment.Oplayers.size(); i++) {
                data = new HashMap<String, Integer>();
                data.put("goals", 0);
                data = new HashMap<String, Integer>();
                data.put("assists", 0);
                data = new HashMap<String, Integer>();
                data.put("off_sides", 0);
                data = new HashMap<String, Integer>();
                data.put("fouls", 0);
                data = new HashMap<String, Integer>();
                data.put("red_cards", 0);
                data = new HashMap<String, Integer>();
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
                time = (TextView) view.findViewById(R.id.time);

                TextView ftn = (TextView) view.findViewById(R.id.first_team_name);
                ftn.setText(SoccerTeamFragment.team);
                TextView stn = (TextView) view.findViewById(R.id.second_team_name);
                stn.setText(SoccerTeamFragment.Oteam);

                TextView ftnp = (TextView) view.findViewById(R.id.first_team_name_poss);
                ftnp.setText(SoccerTeamFragment.team);
                TextView stnp = (TextView) view.findViewById(R.id.second_team_name_poss);
                stnp.setText(SoccerTeamFragment.Oteam);

                final TextView ftp = (TextView) view.findViewById(R.id.first_team_poss);
                final TextView stp = (TextView) view.findViewById(R.id.second_team_poss);
                final TextView extraTimetv = (TextView) view.findViewById(R.id.extra_time);
                final Button start = (Button) view.findViewById(R.id.start_pause);

                if(possession==1){
//                    stp.animate().translationX(-stp.getWidth());
//                    stp.setVisibility(View.INVISIBLE);
                    ftp.setVisibility(View.VISIBLE);
                    ftp.animate().translationX(0);
                    stp.setVisibility(View.VISIBLE);
                    stp.animate().translationX(-stp.getWidth());
                }else{
//                    ftp.animate().translationX(ftp.getWidth());
//                    ftp.setVisibility(View.INVISIBLE);
                    stp.setVisibility(View.VISIBLE);
                    stp.animate().translationX(0);
                    ftp.setVisibility(View.VISIBLE);
                    ftp.animate().translationX(ftp.getWidth());
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
                                    .setIcon(R.drawable.edit_icon)
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
                                            }
                                        }
                                    }).show();
                        }
                        if(msg.getData().containsKey("endExtra")){
                            start.callOnClick();
                            extraTimetv.setText("");
                            if(half==1){
                                half=2;
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
                break;
            case 2:
                view = inflater.inflate(R.layout.first_team_formation, null);

                TextView tn1 = (TextView) view.findViewById(R.id.team_name);
                tn1.setText(SoccerTeamFragment.team+"'s Formation");

                ListView ftList = (ListView) view.findViewById(R.id.players_list);
                fadapter = new PlayerListAdapter(players);
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
                                    fbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            movedView = v;
                                            movedView.setVisibility(View.GONE);
                                            ClipData.Item data = new ClipData.Item(item.getText());
                                            ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                            ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                            v.startDrag(dragData, sb, null, 0);
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
                sadapter = new PlayerListAdapter(Oplayers);
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
                                    sbp.addView(playerOnGround);

                                    playerOnGround.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            movedView = v;
                                            movedView.setVisibility(View.GONE);
                                            ClipData.Item data = new ClipData.Item(item.getText());
                                            ClipData dragData = new ClipData(item.getText(), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                                            ShadowBuilder sb = new ShadowBuilder(playerOnGround, getContext());
                                            v.startDrag(dragData, sb, null, 0);
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

        public PlayerListAdapter(ArrayList<String> lineup) {
            this.lineup = lineup;
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
            final View finalView = view;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    movedView = v;
                    movedView.setVisibility(View.GONE);
                    ClipData.Item data = new ClipData.Item(lineup.get(position));
                    ClipData dragData = new ClipData(lineup.get(position), new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, data);
                    ShadowBuilder sb = new ShadowBuilder(finalView, getContext());
                    v.startDrag(dragData, sb, null, 0);
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
