package com.scorrer.ravi.scorrer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.Inflater;

public class ScoreCardFragment extends Fragment {

    private static final String ARG = "PAGE_NO";
    private static ArrayList<LinkedHashMap<String, Object>> savedState = new ArrayList<LinkedHashMap<String, Object>>();
    private static int undoIndex = 0;
    private static int striker = 1;
    private int pageNo;
    private ArrayList<String> batTeam;
    private ArrayList<String> bolTeam;
    private ArrayList<String> ftusedBatsmen = new ArrayList<>();
    private ArrayList<String> ftusedBowlers = new ArrayList<>();
    private ArrayList<String> stusedBatsmen = new ArrayList<>();
    private ArrayList<String> stusedBowlers = new ArrayList<>();
    private ArrayList<String> outBats;
    private String prevBowler = "";
    private boolean bowlerChanged = false;
    private String batTeamName;
    private String bolTeamName;
    private static int runs = 0;
    private static int wickets = 0;
    private int overs = 0;
    private int overs_balls = 0;
    private float runRate = (float) 0.0;
    private static int inng = 1;
    private static LinkedHashMap<String, HashMap<String, String>> firstTeamBattingStats = new LinkedHashMap<String, HashMap<String, String>>();
    private static LinkedHashMap<String, HashMap<String, String>> secondTeamBattingStats = new LinkedHashMap<String, HashMap<String, String>>();
    private static LinkedHashMap<String, HashMap<String, String>> firstTeamBowlingStats = new LinkedHashMap<String, HashMap<String, String>>();
    private static LinkedHashMap<String, HashMap<String, String>> secondTeamBowlingStats = new LinkedHashMap<String, HashMap<String, String>>();
    private static BattingStatsAdapter firstTeamBattingAdapter;
    private static BattingStatsAdapter secondTeamBattingAdapter;
    private static BowlingStatsAdapter firstTeamBowlingAdapter;
    private static BowlingStatsAdapter secondTeamBowlingAdapter;
    private LinearLayout extraInfoPane;
    private ArrayList<HashMap<String, String>> stats = new ArrayList<HashMap<String, String>>();    //stats of current batsmen
    private TextView co;    //current overs
    private TextView crr;   //current run rate
    private TextView scoreBrief;
    private BatsmanStatsAdapter batsmanStatsAdapter;
    private TextView fblu;
    private TextView fblo;
    private TextView fblr;
    private TextView fblw;
    private TextView fble;
    private String prevBalltype = "";
    private boolean clearPrevBalltype = false;
    private boolean overStarted = false;
    private ArrayList<String> nextBatsman = new ArrayList<String>();
    private ArrayList<String> nextBowler = new ArrayList<String>(); //using it as next bowler as well as next player
    private View cricketScoreBoard;
    private static ViewGroup cricketScoreCard;
    private int requRuns;    //first inng total
    private TextView ovt;
    private static int fit = 0;        //first inngs total
    private static int fiw = 0;
    private static int fie = 0;        //first inng extra
    private static int sie = 0;

    public static ScoreCardFragment newInstance(int page_no){
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, page_no);
        ScoreCardFragment fragment = new ScoreCardFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNo = getArguments().getInt(ARG);
        if(CricketTeamFragment.toss.equals("F")){
            batTeam = CricketTeamFragment.players;
            bolTeam = CricketTeamFragment.Oplayers;
            batTeamName = CricketTeamFragment.team;
            bolTeamName = CricketTeamFragment.Oteam;
        }else{
            batTeam = CricketTeamFragment.Oplayers;
            bolTeam = CricketTeamFragment.players;
            batTeamName = CricketTeamFragment.Oteam;
            bolTeamName = CricketTeamFragment.team;
        }
        HashMap<String, String> statsValue;
        for(int i=0; i<CricketTeamFragment.currentBatsmen.size(); i++){
            //if(CricketTeamFragment.currentBatsmen.contains(batTeam.get(i))) {
                statsValue = new HashMap<String, String>();
                statsValue.put("runs", "0");
                statsValue.put("balls", "0");
                statsValue.put("fours", "0");
                statsValue.put("sixes", "0");
                ftusedBatsmen.add(CricketTeamFragment.currentBatsmen.get(i));
                firstTeamBattingStats.put(CricketTeamFragment.currentBatsmen.get(i), statsValue);
            //}
        }
        for(int i=0; i<CricketTeamFragment.currentBowlers.size(); i++){
            statsValue = new HashMap<String, String>();
            statsValue.put("overs", "0");
            statsValue.put("runs", "0");
            statsValue.put("wickets", "0");
            statsValue.put("eco", "0");
            stusedBowlers.add(CricketTeamFragment.currentBowlers.get(i));
            secondTeamBowlingStats.put(CricketTeamFragment.currentBowlers.get(i), statsValue);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = null;
        switch (pageNo){
            case 1:
                view = inflater.inflate(R.layout.cricket_score_board, container, false);
                cricketScoreBoard = view;

                SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) view.findViewById(R.id.slidingPaneLayout);
                slidingPaneLayout.openPane();

                TextView batTN = (TextView) view.findViewById(R.id.batting_team_name);
                TextView bolTN = (TextView) view.findViewById(R.id.bowling_team_name);
                batTN.setText(batTeamName);
                bolTN.setText(bolTeamName);
                String sb = String.valueOf(runs)+"/"+String.valueOf(wickets);
                scoreBrief = (TextView) view.findViewById(R.id.score_brief);
                scoreBrief.setText(sb);
                co = (TextView) view.findViewById(R.id.cricket_overs);
                crr = (TextView) view.findViewById(R.id.current_rr);
                co.setText(String.valueOf("Overs: "+overs+"."+overs_balls));
                crr.setText(String.valueOf("CRR: "+runRate));

                final HashMap<String, String> statsValue = new HashMap<String, String>();
                ListView batsmanStats = (ListView) view.findViewById(R.id.batsman_stats);
                statsValue.put("runs", "0");
                statsValue.put("balls", "0");
                statsValue.put("fours", "0");
                statsValue.put("sixes", "0");
                stats.add(statsValue);
                stats.add(statsValue);
                batsmanStatsAdapter = new BatsmanStatsAdapter(CricketTeamFragment.currentBatsmen, stats);
                batsmanStats.setAdapter(batsmanStatsAdapter);

                fblu = (TextView) view.findViewById(R.id.first_bowler_username);
                fblu.setText(CricketTeamFragment.currentBowlers.get(0));
                fblo = (TextView) view.findViewById(R.id.first_bowler_overs);
                fblo.setText("0");
                fblr = (TextView) view.findViewById(R.id.first_bowler_runs);
                fblr.setText("0");
                fblw = (TextView) view.findViewById(R.id.first_bowler_wickets);
                fblw.setText("0");
                fble = (TextView) view.findViewById(R.id.first_bowler_eco);
                fble.setText("0");

                LinearLayout oversOV = (LinearLayout) view.findViewById(R.id.overs_ov);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                ovt = new TextView(getContext());
                ovt.setLayoutParams(lp);
                ovt.setText("This over:");
                oversOV.addView(ovt);
                if(inng==1){
                    extraInfoPane = (LinearLayout) view.findViewById(R.id.extra_info_pane);
                    extraInfoPane.setVisibility(View.GONE);
                }
                LinearLayout runsPane = (LinearLayout) view.findViewById(R.id.runs_pane);

                RelativeLayout pullOverMenu = (RelativeLayout) view.findViewById(R.id.pullover_menu);

                Button oneRun = (Button) runsPane.findViewById(R.id.one_run);
                oneRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        ScoreCardFragment.this.runs += 1;
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                            }else{
                                striker = striker%2+1;
                            }
                        }else{
                            striker = striker%2+1;
                        }
                        clearPrevBalltype = true;
                        updater(index, 1, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button twoRun = (Button) runsPane.findViewById(R.id.two_run);
                twoRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        ScoreCardFragment.this.runs += 2;
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES")  || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                striker = striker%2+1;
                                overStarted = false;
                            }
                        }
                        clearPrevBalltype = true;
                        updater(index, 2, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button threeRun = (Button) runsPane.findViewById(R.id.three_run);
                threeRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        ScoreCardFragment.this.runs += 3;
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                            }else{
                                striker = striker%2+1;
                            }
                        }else{
                            clearPrevBalltype = true;
                            striker = striker%2+1;
                        }
                        clearPrevBalltype = true;
                        updater(index, 3, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button fourRun = (Button) runsPane.findViewById(R.id.four_run);
                fourRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        ScoreCardFragment.this.runs += 4;
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                striker = striker%2+1;
                                overStarted = false;
                            }
                        }
                        clearPrevBalltype = true;
                        updater(index, 4, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button fiveRun = (Button) runsPane.findViewById(R.id.five_run);
                fiveRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        ScoreCardFragment.this.runs += 5;
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                            }else{
                                striker = striker%2+1;
                            }
                        }else{
                            striker = striker%2+1;
                        }
                        clearPrevBalltype = true;
                        updater(index, 5, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button sixRun = (Button) runsPane.findViewById(R.id.six_run);
                sixRun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!prevBalltype.equals("WIDE") && !prevBalltype.equals("LEG BYES") && !prevBalltype.equals("BYES")) {
                            saveState();
                            ScoreCardFragment.this.runs += 6;
                            int index = striker-1;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            if (!prevBalltype.equals("NO BALL")) {
                                overs_balls += 1;
                                bowlerChanged = true;
                                if (overs_balls == 6) {
                                    overs++;
                                    overs_balls = 0;
                                    overStarted = false;
                                    striker = striker%2+1;
                                }
                            }
                            clearPrevBalltype = true;
                            updater(index, 6, ovt);
                            if(!overStarted){
                                if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                    changeOfInng();
                                }else{
                                    changeOfBowler();
                                }
                            }
                        }else{
                            Toast.makeText(getContext(), "Cannot score a six on a wide/bye/leg byes ball.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                LinearLayout extrasPane = (LinearLayout) view.findViewById(R.id.extras_pane);
                Button wide = (Button) extrasPane.findViewById(R.id.wide);
                wide.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("")) {
                            saveState();
                            bowlerChanged = true;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            prevBalltype = "WIDE";
                            ScoreCardFragment.this.runs += 1;
                            int index = striker - 1;
                            updater(index, 1, ovt);
                        }
                    }
                });

                Button noBall = (Button) extrasPane.findViewById(R.id.no_ball);
                noBall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("")) {
                            saveState();
                            bowlerChanged = true;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            prevBalltype = "NO BALL";
                            ScoreCardFragment.this.runs += 1;
                            int index = striker - 1;
                            updater(index, 1, ovt);
                        }
                    }
                });

                Button legByes = (Button) extrasPane.findViewById(R.id.leg_byes);
                legByes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("") || prevBalltype.equals("NO BALL")) {
                            saveState();
                            bowlerChanged = true;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            prevBalltype += " LEG BYES";
                            prevBalltype = prevBalltype.trim();
                            int index = striker - 1;
                            updater(index, 0, ovt);
                        }
                    }
                });

                Button byes = (Button) pullOverMenu.findViewById(R.id.byes);
                byes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("") || prevBalltype.equals("NO BALL")) {
                            saveState();
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            prevBalltype += " BYES";
                            prevBalltype = prevBalltype.trim();
                            int index = striker - 1;
                            updater(index, 0, ovt);
                        }
                    }
                });

                Button bowled = (Button) pullOverMenu.findViewById(R.id.bowled);
                bowled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("")) {
                            saveState();
                            bowlerChanged = true;
                            ScoreCardFragment.this.wickets+=1;
                            int index = striker - 1;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            overs_balls += 1;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker % 2 + 1;
                            }
                            prevBalltype = "BOWLED";
                            clearPrevBalltype = true;
                            updater(index, 0, ovt);
                            boolean change = fallOfWicket(index, "BOWLED");
                            if(!overStarted && !change){
                                if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                    changeOfInng();
                                }else{
                                    changeOfBowler();
                                }
                            }
                        }
                    }
                });

                Button lbw = (Button) pullOverMenu.findViewById(R.id.lbw);
                lbw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("")) {
                            saveState();
                            bowlerChanged = true;
                            ScoreCardFragment.this.wickets+=1;
                            int index = striker - 1;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            overs_balls += 1;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker % 2 + 1;
                            }
                            prevBalltype = "LBW";
                            clearPrevBalltype = true;
                            updater(index, 0, ovt);
                            boolean change = fallOfWicket(index, "LBW");
                            if(!overStarted && !change){
                                if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                    changeOfInng();
                                }else{
                                    changeOfBowler();
                                }
                            }
                        }
                    }
                });

                Button caught = (Button) pullOverMenu.findViewById(R.id.caught);
                caught.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("")) {
                            saveState();
                            bowlerChanged = true;
                            ScoreCardFragment.this.wickets+=1;
                            int index = striker - 1;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            overs_balls += 1;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker % 2 + 1;
                            }
                            prevBalltype = "CAUGHT";
                            clearPrevBalltype = true;
                            updater(index, 0, ovt);
                        }
                    }
                });

                Button stump = (Button) pullOverMenu.findViewById(R.id.stump);
                stump.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("") || prevBalltype.equals("WIDE")) {
                            saveState();
                            bowlerChanged = true;
                            ScoreCardFragment.this.wickets+=1;
                            int index = striker - 1;
                            if(prevBalltype.equals("")) {
                                if (!overStarted) {
                                    ovt.setText("This over: ");
                                    overStarted = true;
                                }
                                overs_balls += 1;
                                if (overs_balls == 6) {
                                    overs++;
                                    overs_balls = 0;
                                    overStarted = false;
                                    striker = striker % 2 + 1;
                                }
                                prevBalltype = "STUMP";
                            }else{
                                prevBalltype += " STUMP";
                            }
                            clearPrevBalltype = true;
                            updater(index, 0, ovt);
                        }
                    }
                });

                Button hitWicket = (Button) pullOverMenu.findViewById(R.id.hit_wicket);
                hitWicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(prevBalltype.equals("") || prevBalltype.equals("WIDE")) {
                            saveState();
                            bowlerChanged = true;
                            ScoreCardFragment.this.wickets+=1;
                            int index = striker - 1;
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            overs_balls += 1;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker % 2 + 1;
                            }
                            prevBalltype += " HIT WICKET";
                            prevBalltype = prevBalltype.trim();
                            clearPrevBalltype = true;
                            updater(index, 0, ovt);
                            boolean change = fallOfWicket(index, "");
                            if(!overStarted && !change){
                                if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                    changeOfInng();
                                }else{
                                    changeOfBowler();
                                }
                            }
                        }
                    }
                });

                Button runOut = (Button) pullOverMenu.findViewById(R.id.run_out);
                runOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        bowlerChanged = true;
                        ScoreCardFragment.this.wickets+=1;
                        int index = striker - 1;
                        if(prevBalltype.equals("")) {
                            if (!overStarted) {
                                ovt.setText("This over: ");
                                overStarted = true;
                            }
                            overs_balls += 1;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker % 2 + 1;
                            }
                        }
                        if(prevBalltype.equals("")) {
                            prevBalltype = "RUN OUT";
                        }else{
                            prevBalltype += " RUN OUT";  //without the contact of the bat run out
                        }
                        updater(index, 0, ovt);
                    }
                });

                LinearLayout missPane = (LinearLayout) view.findViewById(R.id.miss_pane);
                Button dotBall = (Button) missPane.findViewById(R.id.dot_ball);
                dotBall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        int index = striker-1;
                        if (!overStarted) {
                            ovt.setText("This over: ");
                            overStarted = true;
                        }
                        if(prevBalltype.equals("") || prevBalltype.equals("LEG BYES")|| prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES RUN OUT")|| prevBalltype.equals("BYES RUN OUT")) {
                            overs_balls += 1;
                            bowlerChanged = true;
                            if (overs_balls == 6) {
                                overs++;
                                overs_balls = 0;
                                overStarted = false;
                                striker = striker%2+1;
                            }
                        }
                        clearPrevBalltype = true;
                        updater(index, 0, ovt);
                        if(!overStarted){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                });

                Button rtdHrt = (Button) missPane.findViewById(R.id.rtd_hrt);
                rtdHrt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveState();
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View view;
                        view = inflater.inflate(R.layout.choose_batsman, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView team_name = (TextView) view.findViewById(R.id.team_name);
                        ListView playersL = (ListView) view.findViewById(R.id.players_name);
                        Button proceed = (Button) view.findViewById(R.id.proceed);
                        PlayerListAdapter Fadapter;
                        title.setText("SELECT RETIRE HEART BATSMAN");
                        ArrayList<String> lineup;
                        lineup = CricketTeamFragment.currentBatsmen;
                        if(inng==1){
                            team_name.setText(batTeamName);
                        }else{
                            team_name.setText(bolTeamName);
                        }
                        Fadapter = new PlayerListAdapter(lineup, "RTDBATSMANSELECTION");
                        playersL.setAdapter(Fadapter);
                        adBuilder.setCustomTitle(view);
                        final AlertDialog alert = adBuilder.create();
                        alert.show();
                        final String ballType = prevBalltype;
                        proceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(nextBowler.size()==1) {
                                    alert.cancel();
                                    int index = CricketTeamFragment.currentBatsmen.indexOf(nextBowler.get(0));
                                    nextBowler.clear();
                                    if(inng==1) {
                                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "rtd hrt");
                                    }else{
                                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "rtd hrt");
                                    }
                                    fallOfWicket(index, "RTD HRT");
                                    if(firstTeamBattingAdapter!=null  && inng==1) {
                                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                                    }
                                }else{
                                    Toast.makeText(getContext(), "Can select max 1 player.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                Button undo = (Button) missPane.findViewById(R.id.undo);
                undo.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        undoAction();
                    }
                });

                Button changeOfBowler = (Button) pullOverMenu.findViewById(R.id.change_of_bowler);
                changeOfBowler.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        changeOfBowler();
                    }
                });

                break;
            case 2:
                view = inflater.inflate(R.layout.cricket_score_card, container, false);
                ScoreCardFragment.cricketScoreCard = container;

                LinearLayout ftbtsc = (LinearLayout) view.findViewById(R.id.first_teams_batting_score_card);
                View battingStrip = inflater.inflate(R.layout.batting_score_strip, null);
                TextView btn = (TextView) battingStrip.findViewById(R.id.team_name);
                btn.setText(batTeamName);
                ftbtsc.addView(battingStrip, 0);

                View view1 = inflater.inflate(R.layout.extras_entry, null);
                LinearLayout ee = (LinearLayout) view1.findViewById(R.id.extra_entry);
                TextView extras = (TextView) ee.findViewById(R.id.extras);
                TextView total = (TextView) ee.findViewById(R.id.total);
                String totVal;
                totVal = runs+"/"+wickets;
                extras.setText(String.valueOf(fie));
                total.setText(totVal);

                ftbtsc.addView(view1, 2);

                ListView ftbts = (ListView) ftbtsc.findViewById(R.id.first_teams_batting_score);
                firstTeamBattingAdapter = new BattingStatsAdapter(ftusedBatsmen, firstTeamBattingStats, 1);
                ftbts.setAdapter(firstTeamBattingAdapter);

                LinearLayout stblsc = (LinearLayout) view.findViewById(R.id.second_teams_bowling_score_card);
                View bowlingStrip = inflater.inflate(R.layout.bowling_score_strip, null);
                TextView bltn = (TextView) bowlingStrip.findViewById(R.id.team_name);
                bltn.setText(bolTeamName);
                stblsc.addView(bowlingStrip, 0);

                ListView stbls = (ListView) stblsc.findViewById(R.id.second_teams_bowling_score);
                secondTeamBowlingAdapter = new BowlingStatsAdapter(stusedBowlers, secondTeamBowlingStats);
                stbls.setAdapter(secondTeamBowlingAdapter);
                if(inng==2){
                    LinearLayout stbtsc = (LinearLayout) view.findViewById(R.id.second_teams_batting_score_card);
                    battingStrip = inflater.inflate(R.layout.batting_score_strip, null);
                    btn = (TextView) battingStrip.findViewById(R.id.team_name);
                    btn.setText(bolTeamName);
                    stbtsc.addView(battingStrip, 0);

                    ftbts = (ListView) stbtsc.findViewById(R.id.second_teams_batting_score);
                    secondTeamBattingAdapter = new BattingStatsAdapter(stusedBatsmen, secondTeamBattingStats, 2);
                    ftbts.setAdapter(secondTeamBattingAdapter);

                    stblsc = (LinearLayout) view.findViewById(R.id.first_teams_bowling_score_card);
                    bowlingStrip = inflater.inflate(R.layout.bowling_score_strip, null);
                    bltn = (TextView) bowlingStrip.findViewById(R.id.team_name);
                    bltn.setText(batTeamName);
                    stblsc.addView(bowlingStrip, 0);

                    view1 = inflater.inflate(R.layout.extras_entry, null);
                    ee = (LinearLayout) view1.findViewById(R.id.extra_entry);
                    extras = (TextView) ee.findViewById(R.id.extras);
                    total = (TextView) ee.findViewById(R.id.total);
                    totVal = runs+"/"+wickets;
                    extras.setText(String.valueOf(sie));
                    total.setText(totVal);

                    stbtsc.addView(view1, 2);

                    stbls = (ListView) stblsc.findViewById(R.id.first_teams_bowling_score);
                    firstTeamBowlingAdapter = new BowlingStatsAdapter(ftusedBowlers, firstTeamBowlingStats);
                    stbls.setAdapter(firstTeamBowlingAdapter);
                }
                break;
        }
        return view;
    }

    private void outBatsman(){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        PlayerListAdapter Fadapter;
        title.setText("SELECT OUT BATSMAN");
        ArrayList<String> lineup;
        lineup = CricketTeamFragment.currentBatsmen;
        if(inng==1){
            team_name.setText(batTeamName);
        }else{
            team_name.setText(bolTeamName);
        }
        Fadapter = new PlayerListAdapter(lineup, "OUTBATSMANSELECTION");
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        final String ballType = prevBalltype;
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextBowler.size()==1) {
                    alert.cancel();
                    int index = CricketTeamFragment.currentBatsmen.indexOf(nextBowler.get(0));
                    nextBowler.clear();
                    getFielder(index, ballType);
                    if(inng==1) {
                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "run out");
                    }else{
                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "run out");
                    }

                    if(firstTeamBattingAdapter!=null  && inng==1) {
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                    }
                }else{
                    Toast.makeText(getContext(), "Can select max 1 player.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectStriker(){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        PlayerListAdapter Fadapter;
        title.setText("BATSMAN ON STRIKE");
        ArrayList<String> lineup;
        lineup = CricketTeamFragment.currentBatsmen;
        if(inng==1){
            team_name.setText(batTeamName);
        }else{
            team_name.setText(bolTeamName);
        }
        Fadapter = new PlayerListAdapter(lineup, "STRIKERSELECTION");
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        final String ballType = prevBalltype;
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nextBowler.size()==1) {
                    alert.cancel();
                    int index = CricketTeamFragment.currentBatsmen.indexOf(nextBowler.get(0));
                    nextBowler.clear();

                    striker = index+1;
                    batsmanStatsAdapter.notifyDataSetChanged();

                    if(firstTeamBattingAdapter!=null  && inng==1) {
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                    }
                }else{
                    Toast.makeText(getContext(), "Can select only 1 player.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeOfBowler(){
        if(inng==1){
            if(secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("overs").equals("0")){
                secondTeamBowlingStats.remove(CricketTeamFragment.currentBowlers.get(0));
                stusedBowlers.remove(CricketTeamFragment.currentBowlers.get(0));
                secondTeamBowlingAdapter.notifyAdapter(stusedBowlers, secondTeamBowlingStats);
            }
        }else{
            if(firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("overs").equals("0")){
                firstTeamBowlingStats.remove(CricketTeamFragment.currentBowlers.get(0));
                ftusedBowlers.remove(CricketTeamFragment.currentBowlers.get(0));
                firstTeamBowlingAdapter.notifyAdapter(ftusedBowlers, firstTeamBowlingStats);
            }
        }

        if(bowlerChanged){
            prevBowler = CricketTeamFragment.currentBowlers.get(0);
            bowlerChanged = false;
        }
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        PlayerListAdapter Fadapter;
        title.setText("SELECT NEXT BOWLER");
        ArrayList<String> lineup = new ArrayList<String>();
        if(inng==1){
            for(int i=0; i<bolTeam.size(); i++) {
                if(!bolTeam.get(i).equals(prevBowler)) {
                    lineup.add(bolTeam.get(i));
                }
            }
            team_name.setText(bolTeamName);
        }else{
            for(int i=0; i<batTeam.size(); i++) {
                if(!batTeam.get(i).equals(prevBowler)) {
                    lineup.add(batTeam.get(i));
                }
            }
            team_name.setText(batTeamName);
        }
        Fadapter = new PlayerListAdapter(lineup, "BOWLERSSELECTION");
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                if(nextBowler.size()==1) {
                    alert.cancel();
                    CricketTeamFragment.currentBowlers.remove(0);
                    CricketTeamFragment.currentBowlers.add(0, nextBowler.get(0));
                    fblu.setText(nextBowler.get(0));
                    if (inng == 1) {
                        String bo;
                        if(secondTeamBowlingStats.containsKey(nextBowler.get(0))){
                            bo = secondTeamBowlingStats.get(nextBowler.get(0)).get("overs");
                        }else{
                            HashMap<String, String> statsValue = new HashMap<String, String>();
                            statsValue.put("overs", "0");
                            statsValue.put("runs", "0");
                            statsValue.put("wickets", "0");
                            statsValue.put("eco", "0");
                            stusedBowlers.add(nextBowler.get(0));
                            secondTeamBowlingStats.put(nextBowler.get(0), statsValue);
                            bo = secondTeamBowlingStats.get(nextBowler.get(0)).get("overs");
                        }

                        String[] aBo = bo.split(Pattern.quote("."));
                        int balls;
                        int Bovers;
                        if (aBo.length == 0 || aBo.length == 1) {
                            balls = 0;
                            Bovers = 0;
                        } else {
                            balls = Integer.valueOf(aBo[1]);
                            Bovers = Integer.valueOf(aBo[0]);
                        }
                        fblo.setText(String.valueOf(Bovers + "." + balls));
                        fblr.setText(secondTeamBowlingStats.get(nextBowler.get(0)).get("runs"));
                        fblw.setText(secondTeamBowlingStats.get(nextBowler.get(0)).get("wickets"));
                        fble.setText(secondTeamBowlingStats.get(nextBowler.get(0)).get("eco"));
                        secondTeamBowlingAdapter.notifyAdapter(stusedBowlers, secondTeamBowlingStats);
                    } else {
                        String bo;
                        if(firstTeamBowlingStats.containsKey(nextBowler.get(0))){
                            bo = firstTeamBowlingStats.get(nextBowler.get(0)).get("overs");
                        }else{
                            HashMap<String, String> statsValue = new HashMap<String, String>();
                            statsValue.put("overs", "0");
                            statsValue.put("runs", "0");
                            statsValue.put("wickets", "0");
                            statsValue.put("eco", "0");
                            ftusedBowlers.add(nextBowler.get(0));
                            firstTeamBowlingStats.put(nextBowler.get(0), statsValue);
                            bo = firstTeamBowlingStats.get(nextBowler.get(0)).get("overs");
                        }
                        String[] aBo = bo.split(Pattern.quote("."));
                        int balls;
                        int Bovers;
                        if (aBo.length == 0 || aBo.length == 1) {
                            balls = 0;
                            Bovers = 0;
                        } else {
                            balls = Integer.valueOf(aBo[1]);
                            Bovers = Integer.valueOf(aBo[0]);
                        }
                        fblo.setText(String.valueOf(Bovers + "." + balls));
                        fblr.setText(firstTeamBowlingStats.get(nextBowler.get(0)).get("runs"));
                        fblw.setText(firstTeamBowlingStats.get(nextBowler.get(0)).get("wickets"));
                        fble.setText(firstTeamBowlingStats.get(nextBowler.get(0)).get("eco"));
                        firstTeamBowlingAdapter.notifyAdapter(ftusedBowlers, firstTeamBowlingStats);
                    }
                    nextBowler.clear();
                }
            }
        });

    }

    private boolean fallOfWicket(final int index, final String balltype){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        PlayerListAdapter Fadapter;
        title.setText("SELECT NEXT BATSMAN");
        ArrayList<String> lineup = new ArrayList<String>();
        if(inng==1) {
            for(int i=0; i<batTeam.size(); i++){
                if(!ftusedBatsmen.contains(batTeam.get(i))){
                    lineup.add(batTeam.get(i));
                }else{
                    if(firstTeamBattingStats.containsKey(batTeam.get(i))){
                        if(firstTeamBattingStats.get(batTeam.get(i)).containsKey("out")) {
                            if (firstTeamBattingStats.get(batTeam.get(i)).get("out").equals("rtd hrt")) {
                                lineup.add(batTeam.get(i));
                            }
                        }
                    }
                }
            }
            Fadapter = new PlayerListAdapter(lineup, "BATSMANSELECTION");
            team_name.setText(batTeamName);
        }else{
            for(int i=0; i<bolTeam.size(); i++){
                if(!ftusedBatsmen.contains(bolTeam.get(i))){
                    lineup.add(bolTeam.get(i));
                }else{
                    if(secondTeamBattingStats.containsKey(bolTeam.get(i))){
                        lineup.add(bolTeam.get(i));
                        if(secondTeamBattingStats.get(bolTeam.get(i)).containsKey("out")) {
                            if (secondTeamBattingStats.get(bolTeam.get(i)).get("out").equals("rtd hrt")) {
                                lineup.add(bolTeam.get(i));
                            }
                        }
                    }
                }
            }
            Fadapter = new PlayerListAdapter(lineup, "BATSMANSELECTION");
            team_name.setText(bolTeamName);
        }
        if(lineup.size()==0 && inng==1){
            changeOfInng();
            return true;
        }

        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LayoutInflater inflater = getActivity().getLayoutInflater();
                if(nextBatsman.size()==1){
                    alert.cancel();
                    CricketTeamFragment.currentBatsmen.remove(index);
                    CricketTeamFragment.currentBatsmen.add(index, nextBatsman.get(0));
                    stats.remove(index);
                    HashMap<String, String> statsValue = new HashMap<String, String>();
                    if(inng==1){
                        if(firstTeamBattingStats.containsKey(nextBatsman.get(0))){
                            statsValue.put("runs", firstTeamBattingStats.get(nextBatsman.get(0)).get("runs"));
                            statsValue.put("balls", firstTeamBattingStats.get(nextBatsman.get(0)).get("balls"));
                            statsValue.put("fours", firstTeamBattingStats.get(nextBatsman.get(0)).get("fours"));
                            statsValue.put("sixes", firstTeamBattingStats.get(nextBatsman.get(0)).get("sixes"));
                        }else{
                            statsValue.put("runs", "0");
                            statsValue.put("balls", "0");
                            statsValue.put("fours", "0");
                            statsValue.put("sixes", "0");
                        }
                    }else{
                        if(secondTeamBattingStats.containsKey(nextBatsman.get(0))){
                            statsValue.put("runs", secondTeamBattingStats.get(nextBatsman.get(0)).get("runs"));
                            statsValue.put("balls", secondTeamBattingStats.get(nextBatsman.get(0)).get("balls"));
                            statsValue.put("fours", secondTeamBattingStats.get(nextBatsman.get(0)).get("fours"));
                            statsValue.put("sixes", secondTeamBattingStats.get(nextBatsman.get(0)).get("sixes"));
                        }else{
                            statsValue.put("runs", "0");
                            statsValue.put("balls", "0");
                            statsValue.put("fours", "0");
                            statsValue.put("sixes", "0");
                        }
                    }
                    HashMap<String, String> statsValue1 = statsValue;
                    stats.add(index, statsValue1);
                    batsmanStatsAdapter.notifyDataSetChanged();
                    if(inng==1) {
                        HashMap<String, String> statsValue2 = statsValue;
                        if(!ftusedBatsmen.contains(nextBatsman.get(0))) {
                            ftusedBatsmen.add(nextBatsman.get(0));
                        }
                        firstTeamBattingStats.put(nextBatsman.get(0), statsValue2);
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else{
                        HashMap<String, String> statsValue2 = statsValue;
                        if(!stusedBatsmen.contains(nextBatsman.get(0))){
                            stusedBatsmen.add(nextBatsman.get(0));
                        }
                        secondTeamBattingStats.put(nextBatsman.get(0), statsValue2);
                        secondTeamBattingAdapter.notifyDataSetChanged();
                    }
                    nextBatsman.clear();
                    if(balltype.equals("CAUGHT")||balltype.equals("RUN OUT")){
                        selectStriker();
                    }
                }else{
                    Toast.makeText(getContext(), "Select one player", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return false;
    }

    private void getFielder(final int index, final String balltype){
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        view = inflater.inflate(R.layout.choose_batsman, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView team_name = (TextView) view.findViewById(R.id.team_name);
        ListView playersL = (ListView) view.findViewById(R.id.players_name);
        Button proceed = (Button) view.findViewById(R.id.proceed);
        PlayerListAdapter Fadapter;
        title.setText("SELECT FIELDER");
        ArrayList<String> lineup = new ArrayList<String>();
        if(inng==1){
            lineup = bolTeam;
            team_name.setText(bolTeamName);
        }else{
            lineup = batTeam;
            team_name.setText(batTeamName);
        }
        Fadapter = new PlayerListAdapter(lineup, "FIELDERSSELECTION");
        playersL.setAdapter(Fadapter);
        adBuilder.setCustomTitle(view);
        final AlertDialog alert = adBuilder.create();
        alert.show();
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                if(nextBowler.size()==1 && !balltype.equals("RUN OUT") && !balltype.equals("NO BALL RUN OUT") && !balltype.equals("WIDE RUN OUT") && !balltype.equals("BYES RUN OUT") && !balltype.equals("LEG BYES RUN OUT")) {
                    alert.cancel();
                    if(inng==1){
                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("second_player", nextBowler.get(0));
                    }else{
                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("second_player", nextBowler.get(0));
                    }
                    nextBowler.clear();
                    if(firstTeamBattingAdapter!=null  && inng==1) {
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                    }
                    if(balltype.equals("CAUGHT")|| balltype.equals("STUMP") || balltype.equals("WIDE STUMP")){
                        boolean change = fallOfWicket(index, "");
                        if(!overStarted && !change){
                            if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                                changeOfInng();
                            }else{
                                changeOfBowler();
                            }
                        }
                    }
                }else if(nextBowler.size()==1 && (balltype.equals("RUN OUT") || balltype.equals("NO BALL RUN OUT") || balltype.equals("WIDE RUN OUT") || balltype.equals("BYES RUN OUT") || balltype.equals("LEG BYES RUN OUT"))){
                    alert.cancel();
                    if(inng==1){
                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", nextBowler.get(0));
                    }else{
                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", nextBowler.get(0));
                    }
                    nextBowler.clear();
                    if(firstTeamBattingAdapter!=null  && inng==1) {
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                    }
                    fallOfWicket(index, "");
                }else if(nextBowler.size()==2 && (balltype.equals("RUN OUT") || balltype.equals("NO BALL RUN OUT") || balltype.equals("WIDE RUN OUT") || balltype.equals("BYES RUN OUT") || balltype.equals("LEG BYES RUN OUT"))){
                    alert.cancel();
                    if(inng==1){
                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", nextBowler.get(0));
                        firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("second_player", nextBowler.get(1));
                    }else{
                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", nextBowler.get(0));
                        secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("second_player", nextBowler.get(1));
                    }
                    nextBowler.clear();
                    if(firstTeamBattingAdapter!=null  && inng==1) {
                        firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                    }else if(secondTeamBattingAdapter!=null  && inng==2) {
                        secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                    }
                    boolean change = fallOfWicket(index, "RUN OUT");
                    if(!overStarted && !change){
                        if(Integer.valueOf(CricketTeamFragment.overs)==overs){
                            changeOfInng();
                        }else{
                            changeOfBowler();
                        }
                    }
                }else{
                    Toast.makeText(getContext(), "Can select max 2 player for run out and 1 for other dismissals.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changeOfInng(){
        if(inng==1){
            Toast.makeText(getContext(), "Change of inngs.", Toast.LENGTH_SHORT).show();
            inng++;
            AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view;
            view = inflater.inflate(R.layout.choose_batsman, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView team_name = (TextView) view.findViewById(R.id.team_name);
            ListView playersL = (ListView) view.findViewById(R.id.players_name);
            Button proceed = (Button) view.findViewById(R.id.proceed);
            PlayerListAdapter Fadapter;
            title.setText("SELECT OPENING BATSMAN");
            ArrayList<String> lineup;
            team_name.setText(bolTeamName);
            Fadapter = new PlayerListAdapter(bolTeam, "BATSMANSELECTION");
            playersL.setAdapter(Fadapter);
            adBuilder.setCustomTitle(view);
            final AlertDialog alert = adBuilder.create();
            alert.show();
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(nextBatsman.size()==2) {
                        alert.cancel();
                        CricketTeamFragment.currentBatsmen.clear();
                        CricketTeamFragment.currentBatsmen.add(nextBatsman.get(0));
                        CricketTeamFragment.currentBatsmen.add(nextBatsman.get(1));
                        nextBatsman.clear();
                        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        final View view;
                        view = inflater.inflate(R.layout.choose_batsman, null);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        TextView team_name = (TextView) view.findViewById(R.id.team_name);
                        ListView playersL = (ListView) view.findViewById(R.id.players_name);
                        Button proceed = (Button) view.findViewById(R.id.proceed);
                        PlayerListAdapter Fadapter;
                        title.setText("SELECT OPENING BOWLER");
                        ArrayList<String> lineup;
                        team_name.setText(batTeamName);
                        Fadapter = new PlayerListAdapter(batTeam, "BOWLERSELECTION");
                        playersL.setAdapter(Fadapter);
                        adBuilder.setCustomTitle(view);
                        final AlertDialog alert = adBuilder.create();
                        alert.show();
                        proceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(nextBowler.size()==1) {
                                    alert.cancel();
                                    CricketTeamFragment.currentBowlers.clear();
                                    CricketTeamFragment.currentBowlers.add(nextBowler.get(0));
                                    nextBowler.clear();
                                    updateUI();
                                }
                            }
                        });
                    }
                }
            });
        }else{
            RelativeLayout uiPane = (RelativeLayout) cricketScoreBoard.findViewById(R.id.ui_pane);
            disable(uiPane);
            View view = cricketScoreBoard;
            String winner;
            int reqR = (requRuns-runs);
            if(reqR<=0){
                winner = bolTeamName+" wins by "+String.valueOf(bolTeam.size()-wickets)+" wickets.";
            }else if(reqR==1){
                winner = "Match tied.";
            }else{
                winner = batTeamName+" wins by "+String.valueOf(reqR-1)+" runs.";
            }
            TextView reqRuns = (TextView) view.findViewById(R.id.req_runs);
            reqRuns.setText(winner);
            TextView reqRR = (TextView) view.findViewById(R.id.req_rr);
            reqRR.setVisibility(View.GONE);
            TextView ballsR = (TextView) view.findViewById(R.id.balls_rem);
            ballsR.setVisibility(View.GONE);;
        }
    }

    private void disable(ViewGroup vg){
        int count = vg.getChildCount();
        if(count>0) {
            for (int i = 0; i < count; i++) {
                if (vg.getChildAt(i) instanceof ViewGroup) {
                    disable((ViewGroup) vg.getChildAt(i));
                } else {
                    vg.getChildAt(i).setEnabled(false);
                }
            }
        }else{
            vg.setEnabled(false);
        }
    }

    private void updateUI(){
        fit = runs;
        fiw = wickets;
        requRuns = runs+1;
        overStarted = false;
        runs = 0;
        wickets = 0;
        overs_balls = 0;
        overs = 0;
        prevBalltype = "";
        clearPrevBalltype = false;
        prevBowler = "";
        bowlerChanged = false;
        runRate = (float) 0.0;

        View view = cricketScoreBoard;
        extraInfoPane.setVisibility(View.VISIBLE);
        TextView reqRuns = (TextView) view.findViewById(R.id.req_runs);
        reqRuns.setText("Req Runs: "+requRuns);
        TextView reqRR = (TextView) view.findViewById(R.id.req_rr);
        int ballsRem = Integer.valueOf(CricketTeamFragment.overs)*6;
        float reqR = (float) ((float) requRuns/ (float) ballsRem)*6;
        reqRR.setText("Req RR: "+String.format("%.2f", reqR));
        TextView ballsR = (TextView) view.findViewById(R.id.balls_rem);
        ballsR.setText("Balls: "+ballsRem);
        TextView batTN = (TextView) view.findViewById(R.id.batting_team_name);
        TextView bolTN = (TextView) view.findViewById(R.id.bowling_team_name);
        batTN.setText(bolTeamName);
        bolTN.setText(batTeamName);
        String sb = String.valueOf(runs)+"/"+String.valueOf(wickets);
        scoreBrief = (TextView) view.findViewById(R.id.score_brief);
        scoreBrief.setText(sb);
        co = (TextView) view.findViewById(R.id.cricket_overs);
        crr = (TextView) view.findViewById(R.id.current_rr);
        co.setText(String.valueOf("Overs: "+overs+"."+overs_balls));
        crr.setText(String.valueOf("CRR: "+runRate));

        stats.clear();
        HashMap<String, String> statsValue = new HashMap<String, String>();
        ListView batsmanStats = (ListView) view.findViewById(R.id.batsman_stats);
        statsValue.put("runs", "0");
        statsValue.put("balls", "0");
        statsValue.put("fours", "0");
        statsValue.put("sixes", "0");
        stats.add(statsValue);
        stats.add(statsValue);
        batsmanStatsAdapter = new BatsmanStatsAdapter(CricketTeamFragment.currentBatsmen, stats);
        batsmanStats.setAdapter(batsmanStatsAdapter);

        fblu = (TextView) view.findViewById(R.id.first_bowler_username);
        fblu.setText(CricketTeamFragment.currentBowlers.get(0));
        fblo = (TextView) view.findViewById(R.id.first_bowler_overs);
        fblo.setText("0");
        fblr = (TextView) view.findViewById(R.id.first_bowler_runs);
        fblr.setText("0");
        fblw = (TextView) view.findViewById(R.id.first_bowler_wickets);
        fblw.setText("0");
        fble = (TextView) view.findViewById(R.id.first_bowler_eco);
        fble.setText("0");

        for(int i=0; i<CricketTeamFragment.currentBatsmen.size(); i++){
            statsValue = new HashMap<String, String>();
            statsValue.put("runs", "0");
            statsValue.put("balls", "0");
            statsValue.put("fours", "0");
            statsValue.put("sixes", "0");
            stusedBatsmen.add(CricketTeamFragment.currentBatsmen.get(i));
            secondTeamBattingStats.put(CricketTeamFragment.currentBatsmen.get(i), statsValue);
        }
        for(int i=0; i<CricketTeamFragment.currentBowlers.size(); i++){
            statsValue = new HashMap<String, String>();
            statsValue.put("overs", "0");
            statsValue.put("runs", "0");
            statsValue.put("wickets", "0");
            statsValue.put("eco", "0");
            ftusedBowlers.add(CricketTeamFragment.currentBowlers.get(i));
            firstTeamBowlingStats.put(CricketTeamFragment.currentBowlers.get(i), statsValue);
        }

        if(cricketScoreCard!=null) {
            view = cricketScoreCard;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout stbtsc = (LinearLayout) view.findViewById(R.id.second_teams_batting_score_card);
            View battingStrip = inflater.inflate(R.layout.batting_score_strip, null);
            TextView btn = (TextView) battingStrip.findViewById(R.id.team_name);
            btn.setText(bolTeamName);
            stbtsc.addView(battingStrip, 0);

            ListView ftbts = (ListView) stbtsc.findViewById(R.id.second_teams_batting_score);
            secondTeamBattingAdapter = new BattingStatsAdapter(stusedBatsmen, secondTeamBattingStats, 2);
            ftbts.setAdapter(secondTeamBattingAdapter);

            LinearLayout stblsc = (LinearLayout) view.findViewById(R.id.first_teams_bowling_score_card);
            View bowlingStrip = inflater.inflate(R.layout.bowling_score_strip, null);
            TextView bltn = (TextView) bowlingStrip.findViewById(R.id.team_name);
            bltn.setText(batTeamName);
            stblsc.addView(bowlingStrip, 0);

            View view1 = inflater.inflate(R.layout.extras_entry, null);
            LinearLayout ee = (LinearLayout) view1.findViewById(R.id.extra_entry);
            TextView extras = (TextView) ee.findViewById(R.id.extras);
            TextView total = (TextView) ee.findViewById(R.id.total);
            String totVal;
            totVal = runs+"/"+wickets;
            extras.setText(String.valueOf(sie));
            total.setText(totVal);

            stbtsc.addView(view1, 2);

            ListView stbls = (ListView) stblsc.findViewById(R.id.first_teams_bowling_score);
            firstTeamBowlingAdapter = new BowlingStatsAdapter(ftusedBowlers, firstTeamBowlingStats);
            stbls.setAdapter(firstTeamBowlingAdapter);
        }
    }

    private void saveState(){
        LinkedHashMap<String, Object> savedState = new LinkedHashMap<String, Object>();
        savedState.put("runs", runs);
        savedState.put("balls", overs_balls);
        savedState.put("overs", overs);
        savedState.put("inng", inng);
        savedState.put("striker", striker);
        ArrayList<String> temp1 = new ArrayList<String>();
        temp1.addAll(ftusedBatsmen);
        savedState.put("ftusedBastman", temp1);
        temp1 = new ArrayList<String>();
        temp1.addAll(stusedBatsmen);
        savedState.put("stusedBatsmen", temp1);
        temp1 = new ArrayList<String>();
        temp1.addAll(ftusedBowlers);
        savedState.put("ftusedBowlers", temp1);
        temp1 = new ArrayList<String>();
        temp1.addAll(stusedBowlers);
        savedState.put("stusedBowlers", temp1);
        savedState.put("firstTeamBattingStats", copyData(firstTeamBattingStats));
        savedState.put("secondTeamBattingStats", copyData(secondTeamBattingStats));
        savedState.put("firstTeamBowlingStats", copyData(firstTeamBowlingStats));
        savedState.put("secondTeamBowlingStats", copyData(secondTeamBowlingStats));
        ArrayList<HashMap<String, String>> temp = new ArrayList<HashMap<String, String>>();
        for (int i=0; i<stats.size(); i++){
            HashMap<String, String> sData = new HashMap<String, String>();
            sData.putAll(stats.get(i));
            temp.add(i, sData);
        }
        savedState.put("stats", temp);
        savedState.put("ovtText", ovt.getText().toString());
        savedState.put("clearPrevBalltype", clearPrevBalltype);
        savedState.put("prevBalltype", prevBalltype);
        savedState.put("overStarted", overStarted);
        savedState.put("prevBowler", prevBowler);
        savedState.put("firstBats", CricketTeamFragment.currentBatsmen.get(0));
        savedState.put("secondBats", CricketTeamFragment.currentBatsmen.get(1));
        savedState.put("firstBowler", CricketTeamFragment.currentBowlers.get(0));
        savedState.put("fit", fit);
        savedState.put("fie", fie);
        savedState.put("fiw", fiw);
        savedState.put("sie", sie);
        ScoreCardFragment.savedState.add(savedState);
        undoIndex++;
    }

    private LinkedHashMap<String, HashMap<String, String>> copyData(LinkedHashMap<String, HashMap<String, String>> copyData){
        LinkedHashMap<String, HashMap<String, String>> temp = new LinkedHashMap<String, HashMap<String, String>>();
        for(LinkedHashMap.Entry entry: copyData.entrySet()){
            HashMap<String, String> inter = new HashMap<String, String>();
            inter.putAll((Map<? extends String, ? extends String>) entry.getValue());
            temp.put((String) entry.getKey(), inter);
        }
        return temp;
    }

    private synchronized void undoAction(){
        undoIndex--;
        if(undoIndex>=0) {
            LinkedHashMap<String, Object> savedState = ScoreCardFragment.savedState.get(undoIndex);
            ScoreCardFragment.savedState.remove(undoIndex);
            runs = (int) savedState.get("runs");
            overs_balls = (int) savedState.get("balls");
            overs = (int) savedState.get("overs");
            inng = (int) savedState.get("inng");
            striker = (int) savedState.get("striker");
            ftusedBatsmen = (ArrayList<String>) savedState.get("ftusedBastman");
            stusedBatsmen = (ArrayList<String>) savedState.get("stusedBatsmen");
            ftusedBowlers = (ArrayList<String>) savedState.get("ftusedBowlers");
            stusedBowlers = (ArrayList<String>) savedState.get("stusedBowlers");
            firstTeamBattingStats = (LinkedHashMap<String, HashMap<String, String>>) savedState.get("firstTeamBattingStats");
            secondTeamBattingStats = (LinkedHashMap<String, HashMap<String, String>>) savedState.get("secondTeamBattingStats");
            firstTeamBowlingStats = (LinkedHashMap<String, HashMap<String, String>>) savedState.get("firstTeamBowlingStats");
            secondTeamBowlingStats = (LinkedHashMap<String, HashMap<String, String>>) savedState.get("secondTeamBowlingStats");
            stats = (ArrayList<HashMap<String, String>>) savedState.get("stats");
            String ovtText = (String) savedState.get("ovtText").toString();
            clearPrevBalltype = (boolean) savedState.get("clearPrevBalltype");
            prevBalltype = (String) savedState.get("prevBalltype");
            overStarted = (boolean) savedState.get("overStarted");
            prevBowler = (String) savedState.get("prevBowler");
            fit = (int) savedState.get("fit");
            fiw = (int) savedState.get("fiw");
            fie = (int) savedState.get("fie");
            sie = (int) savedState.get("sie");
            CricketTeamFragment.currentBatsmen.clear();
            CricketTeamFragment.currentBatsmen.add((String) savedState.get("firstBats"));
            CricketTeamFragment.currentBatsmen.add((String) savedState.get("secondBats"));
            CricketTeamFragment.currentBowlers.clear();
            CricketTeamFragment.currentBowlers.add((String) savedState.get("firstBowler"));

            View view;
            view = cricketScoreBoard;
            TextView batTN = (TextView) view.findViewById(R.id.batting_team_name);
            TextView bolTN = (TextView) view.findViewById(R.id.bowling_team_name);
            if (inng == 1) {
                batTN.setText(batTeamName);
                bolTN.setText(bolTeamName);
            } else {
                batTN.setText(bolTeamName);
                bolTN.setText(batTeamName);
            }
            String sb = String.valueOf(runs) + "/" + String.valueOf(wickets);
            scoreBrief = (TextView) view.findViewById(R.id.score_brief);
            scoreBrief.setText(sb);
            co = (TextView) view.findViewById(R.id.cricket_overs);
            crr = (TextView) view.findViewById(R.id.current_rr);
            co.setText(String.valueOf("Overs: " + overs + "." + overs_balls));
            int totalBalls = overs * 6 + overs_balls;
            float runRate = 0;
            if (totalBalls != 0) {
                runRate = (float) (6 * runs) / (float) totalBalls;
            }
            crr.setText("Run Rate: " + String.format("%.2f", runRate));

            batsmanStatsAdapter.notifyAdapter(CricketTeamFragment.currentBatsmen, stats);

            ovt.setText(ovtText);
            if (inng == 1) {
                extraInfoPane = (LinearLayout) view.findViewById(R.id.extra_info_pane);
                extraInfoPane.setVisibility(View.GONE);
            } else if (inng == 2) {
                view = cricketScoreBoard;
                extraInfoPane.setVisibility(View.VISIBLE);
                int reqRu = requRuns - runs;
                TextView reqRuns = (TextView) view.findViewById(R.id.req_runs);
                reqRuns.setText("Req Runs: " + reqRu);
                TextView reqRR = (TextView) view.findViewById(R.id.req_rr);
                int ballsRem = Integer.valueOf(CricketTeamFragment.overs) * 6 - overs * 6 - overs_balls;
                float reqR = (float) ((float) reqRu / (float) ballsRem) * 6;
                reqRR.setText("Req RR: " + String.format("%.2f", reqR));
                TextView ballsR = (TextView) view.findViewById(R.id.balls_rem);
                ballsR.setText("Balls: " + ballsRem);
            }

            fblu.setText(CricketTeamFragment.currentBowlers.get(0));
            if (inng == 1) {
                String bo = secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("overs");
                String[] aBo = bo.split(Pattern.quote("."));
                int balls;
                int Bovers;
                if (aBo.length == 0 || aBo.length == 1) {
                    balls = 0;
                    Bovers = 0;
                } else {
                    balls = Integer.valueOf(aBo[1]);
                    Bovers = Integer.valueOf(aBo[0]);
                }
                fblo.setText(String.valueOf(Bovers + "." + balls));
                fblr.setText(secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("runs"));
                fblw.setText(secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("wickets"));
                fble.setText(secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("eco"));
                secondTeamBowlingAdapter.notifyAdapter(stusedBowlers, secondTeamBowlingStats);
            } else {
                String bo = firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("overs");
                String[] aBo = bo.split(Pattern.quote("."));
                int balls;
                int Bovers;
                if (aBo.length == 0 || aBo.length == 1) {
                    balls = 0;
                    Bovers = 0;
                } else {
                    balls = Integer.valueOf(aBo[1]);
                    Bovers = Integer.valueOf(aBo[0]);
                }
                fblo.setText(String.valueOf(Bovers + "." + balls));
                fblr.setText(firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("runs"));
                fblw.setText(firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("wickets"));
                fble.setText(firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).get("eco"));
                firstTeamBowlingAdapter.notifyAdapter(ftusedBowlers, firstTeamBowlingStats);
            }

            view = cricketScoreCard;
            firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
            secondTeamBowlingAdapter.notifyDataSetChanged();
            if (inng == 2) {
                secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                firstTeamBowlingAdapter.notifyDataSetChanged();
            } else {
                LinearLayout stbtsc = (LinearLayout) view.findViewById(R.id.second_teams_batting_score_card);
                if (stbtsc.getChildCount() > 1)
                    stbtsc.removeViewAt(0);

                ListView ftbts = (ListView) stbtsc.findViewById(R.id.second_teams_batting_score);
                secondTeamBattingAdapter = new BattingStatsAdapter(stusedBatsmen, secondTeamBattingStats, 2);
                ftbts.setAdapter(secondTeamBattingAdapter);
                secondTeamBattingAdapter.notifyDataSetChanged();

                LinearLayout stblsc = (LinearLayout) view.findViewById(R.id.first_teams_bowling_score_card);
                if (stblsc.getChildCount() > 1) {
                    stblsc.removeViewAt(0);
                    stblsc.removeViewAt(2);
                }

                ListView stbls = (ListView) stblsc.findViewById(R.id.first_teams_bowling_score);
                firstTeamBowlingAdapter = new BowlingStatsAdapter(ftusedBowlers, firstTeamBowlingStats);
                stbls.setAdapter(firstTeamBowlingAdapter);
                firstTeamBowlingAdapter.notifyDataSetChanged();
            }
            updateTotal();
        }else{
            undoIndex = 0;
        }

    }

    private void updateTotal(){
        if(inng==1) {
            View view1 = cricketScoreCard.findViewById(R.id.first_teams_batting_score_card);
            LinearLayout ee = (LinearLayout) view1.findViewById(R.id.extra_entry);
            TextView extras = (TextView) ee.findViewById(R.id.extras);
            TextView total = (TextView) ee.findViewById(R.id.total);
            String totVal;
            totVal = runs + "/" + wickets;
            extras.setText(String.valueOf(fie));
            total.setText(totVal);
        }else{
            View view1 = cricketScoreCard.findViewById(R.id.second_teams_batting_score_card);
            LinearLayout ee = (LinearLayout) view1.findViewById(R.id.extra_entry);
            TextView extras = (TextView) ee.findViewById(R.id.extras);
            TextView total = (TextView) ee.findViewById(R.id.total);
            String totVal;
            totVal = runs + "/" + wickets;
            extras.setText(String.valueOf(sie));
            total.setText(totVal);
        }
    }

    private void updater(int index, int add_runs, TextView ovt){
        scoreBrief.setText(String.valueOf(this.runs+"/"+this.wickets));
        co.setText(String.valueOf("Overs: "+overs+"."+overs_balls));
        int totalBalls = overs*6 + overs_balls;
        float runRate = 0;
        if(totalBalls!=0) {
            runRate = (float) (6 * runs) / (float) totalBalls;
        }
        crr.setText("Run Rate: "+ String.format("%.2f", runRate));
        switch (prevBalltype){
            case "":
                ovt.append(String.valueOf(add_runs+" "));
                break;
            case "WIDE":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("WD+"));
                }
                break;
            case "NO BALL":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("NB+"));
                }
                break;
            case "LEG BYES":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("LB+"));
                }
                break;
            case "NO BALL LEG BYES":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("LB+"));
                }
                break;
            case "BYES":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("B+"));
                }
                break;
            case "NO BALL BYES":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("B+"));
                }
                break;
            case "BOWLED":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "LBW":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "HIT WICKET":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "WIDE HIT WICKET":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "CAUGHT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "STUMP":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "WIDE STUMP":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf("W "));
                }
                break;
            case "RUN OUT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("W+"));
                }
                break;
            case "BYES RUN OUT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("W+"));
                }
                break;
            case "LEG BYES RUN OUT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("W+"));
                }
                break;
            case "WIDE RUN OUT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("W+"));
                }
                break;
            case "NO BALL RUN OUT":
                if(clearPrevBalltype){
                    ovt.append(String.valueOf(add_runs+" "));
                }else{
                    ovt.append(String.valueOf("W+"));
                }
                break;
        }

        if(prevBalltype.equals("WIDE") || prevBalltype.equals("NO BALL") || prevBalltype.equals("LEG BYES") || prevBalltype.equals("BYES") || prevBalltype.equals("NO BALL LEG BYES") || prevBalltype.equals("NO BALL BYES") || prevBalltype.equals("NO BALL RUN OUT") || prevBalltype.equals("WIDE RUN OUT")){
            if(inng==1){
                if(prevBalltype.equals("NO BALL")) {
                    if(clearPrevBalltype) {
                        fie += 1;
                    }
                }else if(prevBalltype.equals("NO BALL LEG BYES") || prevBalltype.equals("NO BALL BYES") || prevBalltype.equals("NO BALL RUN OUT") || prevBalltype.equals("WIDE RUN OUT")){
                    if(clearPrevBalltype){
                        fie += 1;
                    }else{
                        fie += add_runs;
                    }
                }else{
                    fie += add_runs;
                }
            }else{
                if(prevBalltype.equals("NO BALL")) {
                    if(clearPrevBalltype) {
                        sie += 1;
                    }
                }else if(prevBalltype.equals("NO BALL LEG BYES") || prevBalltype.equals("NO BALL BYES") || prevBalltype.equals("WIDE RUN OUT")){
                    if(clearPrevBalltype){
                        sie += 1;
                    }else{
                        sie += add_runs;
                    }
                }else{
                    sie += add_runs;
                }
            }
        }

        if(prevBalltype.equals("CAUGHT") || prevBalltype.equals("NO BALL") || prevBalltype.equals("") || prevBalltype.equals("LBW") || prevBalltype.equals("BOWLED") || prevBalltype.equals("HIT WICKET") || prevBalltype.equals("RUN OUT") || prevBalltype.equals("NO BALL RUN OUT") || prevBalltype.equals("BYES") || prevBalltype.equals("LEG BYES") || prevBalltype.equals("LEG BYES RUN OUT") || prevBalltype.equals("BYES RUN OUT") || prevBalltype.equals("WIDE RUN OUT")) {
            HashMap<String, String> temp;
            temp = new HashMap<String, String>();
            int runs = Integer.valueOf(stats.get(index).get("runs"));
            if(!prevBalltype.equals("BYES") && !prevBalltype.equals("LBW") && !prevBalltype.equals("LEG BYES") && !prevBalltype.equals("WIDE RUN OUT") && !prevBalltype.equals("LEG BYES RUN OUT") && !prevBalltype.equals("BYES RUN OUT")) {
                if(clearPrevBalltype) {
                    runs += add_runs;
                }
            }
            int balls = Integer.valueOf(stats.get(index).get("balls"));
            if(clearPrevBalltype && !prevBalltype.equals("WIDE RUN OUT")) {
                balls++;
            }
            int fours = Integer.valueOf(stats.get(index).get("fours"));
            int sixes = Integer.valueOf(stats.get(index).get("sixes"));
            if (add_runs == 4) {
                fours++;
            } else if (add_runs == 6) {
                sixes++;
            }
            temp.put("runs", String.valueOf(runs));
            temp.put("balls", String.valueOf(balls));
            temp.put("fours", String.valueOf(fours));
            temp.put("sixes", String.valueOf(sixes));
            stats.remove(index);
            stats.add(index, temp);
            if(inng==1){
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("runs");
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("balls");
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("fours");
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("sixes");
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("runs", String.valueOf(runs));
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("balls", String.valueOf(balls));
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("fours", String.valueOf(fours));
                firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("sixes", String.valueOf(sixes));
                if(prevBalltype.equals("BOWLED")){
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "bowled");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("LBW")){
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "lbw");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("HIT WICKET")){
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "hit wicket");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("STUMP")){
                    getFielder(index, prevBalltype);
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "stump");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("WIDE STUMP")){
                    getFielder(index, prevBalltype);
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "stump");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("CAUGHT")){
                    getFielder(index, prevBalltype);
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "caught");
                    firstTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("RUN OUT") || prevBalltype.equals("NO BALL RUN OUT") || prevBalltype.equals("LEG BYES RUN OUT") || prevBalltype.equals("BYES RUN OUT") || prevBalltype.equals("WIDE RUN OUT")){
                    if(clearPrevBalltype) {
                        outBatsman();
                    }
                }
                if(firstTeamBattingAdapter!=null  && inng==1) {
                    firstTeamBattingAdapter.notifyAdapter(ftusedBatsmen, firstTeamBattingStats);
                }
            }else{
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("runs");
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("balls");
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("fours");
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).remove("sixes");
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("runs", String.valueOf(runs));
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("balls", String.valueOf(balls));
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("fours", String.valueOf(fours));
                secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("sixes", String.valueOf(sixes));
                if(prevBalltype.equals("BOWLED")){
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "bowled");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("LBW")){
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "lbw");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("HIT WICKET")){
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "hit wicket");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("STUMP")){
                    getFielder(index, prevBalltype);
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "stump");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals(" WIDE STUMP")){
                    getFielder(index, prevBalltype);
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "stump");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("CAUGHT")){
                    getFielder(index, prevBalltype);
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("out", "caught");
                    secondTeamBattingStats.get(CricketTeamFragment.currentBatsmen.get(index)).put("first_player", CricketTeamFragment.currentBowlers.get(0));
                }else if(prevBalltype.equals("RUN OUT") || prevBalltype.equals("NO BALL RUN OUT") || prevBalltype.equals("LEG BYES RUN OUT") || prevBalltype.equals("BYES RUN OUT") || prevBalltype.equals("WIDE RUN OUT")){
                    if(clearPrevBalltype) {
                        outBatsman();
                    }
                }
                if(secondTeamBattingAdapter!=null  && inng==2) {
                    secondTeamBattingAdapter.notifyAdapter(stusedBatsmen, secondTeamBattingStats);
                }
            }
        }

        String bo = fblo.getText().toString();
        String[] aBo = bo.split(Pattern.quote("."));
        int balls;
        int Bovers;
        if (aBo.length == 0 || aBo.length == 1) {
            balls = 0;
            Bovers = 0;
        } else {
            balls = Integer.valueOf(aBo[1]);
            Bovers = Integer.valueOf(aBo[0]);
        }
        if(!prevBalltype.equals("NO BALL") && !prevBalltype.equals("NO BALL LEG BYES") && !prevBalltype.equals("NO BALL BYES") && !prevBalltype.equals("WIDE") && !prevBalltype.equals("WIDE STUMP") && !prevBalltype.equals("WIDE RUN OUT") && !prevBalltype.equals("NO BALL RUN OUT") && !prevBalltype.equals("WIDE HIT WICKET")) {
            if(clearPrevBalltype || prevBalltype.equals("")) {
                balls += 1;
                if (balls == 6) {
                    balls = 0;
                    Bovers += 1;
                }
                fblo.setText(String.valueOf(Bovers + "." + balls));
                if(inng==1){
                    secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("overs");
                    secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("overs", String.valueOf(Bovers + "." + balls));
                }else{
                    firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("overs");
                    firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("overs", String.valueOf(Bovers + "." + balls));
                }
            }
        }
        int Bruns = 0;
        if(!prevBalltype.equals("WIDE STUMP") && !prevBalltype.equals("LEG BYES") && !prevBalltype.equals("BYES") && !prevBalltype.equals("LEG BYES RUN OUT") && !prevBalltype.equals("BYES RUN OUT")) {
            Bruns = Integer.valueOf(fblr.getText().toString());
            Bruns += add_runs;
            fblr.setText(String.valueOf(Bruns));
            if(inng==1){
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("runs");
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("runs", String.valueOf(Bruns));
            }else{
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("runs");
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("runs", String.valueOf(Bruns));
            }
        }
        if(prevBalltype.equals("LBW") || prevBalltype.equals("BOWLED") || prevBalltype.equals("STUMP")|| prevBalltype.equals("WIDE STUMP") || prevBalltype.equals("CAUGHT") || prevBalltype.equals("HIT WICKET") || prevBalltype.equals("WIDE STUMP")){
            int Bwickets = Integer.valueOf(fblw.getText().toString());
            Bwickets++;
            fblw.setText(String.valueOf(Bwickets));
            if(inng==1){
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("wickets");
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("wickets", String.valueOf(Bwickets));
            }else{
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("wickets");
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("wickets", String.valueOf(Bwickets));
            }
        }
        int bBalls = Bovers*6 + balls;
        float ecoRate = 0;
        if(bBalls!=0) {
            ecoRate = (float) (6 * Bruns) / (float) bBalls;
            if(inng==1){
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("eco");
                secondTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("eco", String.format("%.2f", ecoRate));
            }else{
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).remove("eco");
                firstTeamBowlingStats.get(CricketTeamFragment.currentBowlers.get(0)).put("eco", String.format("%.2f", ecoRate));
            }
        }
        fble.setText(String.format("%.2f", ecoRate));
        if(clearPrevBalltype){
            prevBalltype = "";
            clearPrevBalltype = false;
        }
        if(inng==2){
            View view = cricketScoreBoard;
            extraInfoPane.setVisibility(View.VISIBLE);
            int reqRu = requRuns-runs;
            if(reqRu<=0){
                reqRu=0;
                changeOfInng();
            }
            TextView reqRuns = (TextView) view.findViewById(R.id.req_runs);
            reqRuns.setText("Req Runs: "+reqRu);
            TextView reqRR = (TextView) view.findViewById(R.id.req_rr);
            int ballsRem = Integer.valueOf(CricketTeamFragment.overs)*6-overs*6-overs_balls;
            float reqR = (float) ((float) reqRu/ (float) ballsRem)*6;
            reqRR.setText("Req RR: "+String.format("%.2f", reqR));
            TextView ballsR = (TextView) view.findViewById(R.id.balls_rem);
            ballsR.setText("Balls: "+ballsRem);
        }

        updateTotal();

        if(secondTeamBowlingAdapter!=null && inng==1) {
            secondTeamBowlingAdapter.notifyDataSetChanged();
        }else if(firstTeamBowlingAdapter!=null){
            firstTeamBowlingAdapter.notifyDataSetChanged();
        }
        batsmanStatsAdapter.notifyDataSetChanged();
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
                    if(action.equals("BATSMANSELECTION")){
                        if(!nextBatsman.contains(plN.getText().toString())) {
                            nextBatsman.add(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG));
                            }
                        }else{
                            nextBatsman.remove(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                            }
                        }
                    }else{
                        if(!nextBowler.contains(plN.getText().toString())) {
                            nextBowler.add(plN.getText().toString());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG, null));
                            } else {
                                v.setBackgroundColor(getResources().getColor(R.color.grayBG));
                            }
                        }else{
                            nextBowler.remove(plN.getText().toString());
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

    class BatsmanStatsAdapter extends BaseAdapter{

        ArrayList<String> batsmanUsernames;
        ArrayList<HashMap<String, String>> batsmanStats;

        public BatsmanStatsAdapter(ArrayList<String> batsmanUsernames, ArrayList<HashMap<String, String>> batsmanStats){
            this.batsmanUsernames = batsmanUsernames;
            this.batsmanStats = batsmanStats;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return batsmanUsernames.get(position);
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
                view = inflater.inflate(R.layout.batsman_scoreboard_entry, null);
            }
            else {
                view = convertView;
            }
            TextView fbu = (TextView) view.findViewById(R.id.first_batsman_username);
            String bos = null;
            if(striker==1){
                bos = batsmanUsernames.get(0)+"*"; //batsman on strike
            }else{
                bos = batsmanUsernames.get(0); //batsman on strike
            }
            fbu.setText(bos);
            TextView fbr = (TextView) view.findViewById(R.id.first_batsman_runs);
            fbr.setText(batsmanStats.get(0).get("runs"));
            TextView fbb = (TextView) view.findViewById(R.id.first_batsman_balls);
            fbb.setText(batsmanStats.get(0).get("balls"));
            TextView fb4 = (TextView) view.findViewById(R.id.first_batsman_fours);
            fb4.setText(batsmanStats.get(0).get("fours"));
            TextView fb6 = (TextView) view.findViewById(R.id.first_batsman_sixes);
            fb6.setText(batsmanStats.get(0).get("sixes"));

            if(striker==2){
                bos = batsmanUsernames.get(1)+"*"; //batsman on strike
            }else{
                bos = batsmanUsernames.get(1); //batsman on strike
            }

            TextView sbu = (TextView) view.findViewById(R.id.second_batsman_username);
            sbu.setText(bos);
            TextView sbr = (TextView) view.findViewById(R.id.second_batsman_runs);
            sbr.setText(batsmanStats.get(1).get("runs"));
            TextView sbb = (TextView) view.findViewById(R.id.second_batsman_balls);
            sbb.setText(batsmanStats.get(1).get("balls"));
            TextView sb4 = (TextView) view.findViewById(R.id.second_batsman_fours);
            sb4.setText(batsmanStats.get(1).get("fours"));
            TextView sb6 = (TextView) view.findViewById(R.id.second_batsman_sixes);
            sb6.setText(batsmanStats.get(1).get("sixes"));
            return  view;
        }

        public void notifyAdapter(ArrayList<String> batsmanUsernames, ArrayList<HashMap<String, String>> batsmanStats){
            this.batsmanUsernames = batsmanUsernames;
            this.batsmanStats = batsmanStats;
            notifyDataSetChanged();
        }
    }

    class BattingStatsAdapter extends BaseAdapter{

        ArrayList<String> lineup;
        LinkedHashMap<String, HashMap<String, String>> batsmansStats;
        int team;

        public BattingStatsAdapter(ArrayList<String> lineup, LinkedHashMap<String, HashMap<String, String>> batsmansStats, int team) {
            this.lineup = lineup;
            this.batsmansStats = batsmansStats;
            this.team = team;
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
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.batting_score_entry, null);
            }
            else {
                view = convertView;
            }
            TextView username = (TextView) view.findViewById(R.id.username);
            username.setText(lineup.get(position));
            if(batsmansStats.get(lineup.get(position))==null){
                return view;
            }
            TextView runs = (TextView) view.findViewById(R.id.runs);
            runs.setText(batsmansStats.get(lineup.get(position)).get("runs"));
            TextView balls = (TextView) view.findViewById(R.id.balls);
            balls.setText(batsmansStats.get(lineup.get(position)).get("balls"));
            TextView fours = (TextView) view.findViewById(R.id.fours);
            fours.setText(batsmansStats.get(lineup.get(position)).get("fours"));
            TextView sixes = (TextView) view.findViewById(R.id.sixes);
            sixes.setText(batsmansStats.get(lineup.get(position)).get("sixes"));

            float sr = 0;
            if(!batsmansStats.get(lineup.get(position)).get("balls").equals("0"))
                sr = ( Float.valueOf(batsmansStats.get(lineup.get(position)).get("runs"))/Float.valueOf(batsmansStats.get(lineup.get(position)).get("balls")) )*Float.valueOf(100);

            TextView strR = (TextView) view.findViewById(R.id.strk_rate);
            strR.setText(String.format("%.2f", sr));

            sr=0;
            if(!batsmansStats.get(lineup.get(position)).get("runs").equals("0"))
                sr = ( (Float.valueOf(4)*Float.valueOf(batsmansStats.get(lineup.get(position)).get("fours")) + Float.valueOf(6)*Float.valueOf(batsmansStats.get(lineup.get(position)).get("sixes")))/ Float.valueOf(batsmansStats.get(lineup.get(position)).get("runs")) )*Float.valueOf(100);

            TextView power_hit = (TextView) view.findViewById(R.id.power_hit);
            power_hit.setText(String.format("%.2f", sr));

            TextView wickets = (TextView) view.findViewById(R.id.out_status);
            if(batsmansStats.get(lineup.get(position)).containsKey("out")) {
                String out;
                if(batsmansStats.get(lineup.get(position)).containsKey("second_player")){
                    out = batsmansStats.get(lineup.get(position)).get("out");
                    if(!out.equals("run out")) {
                        out += " " + batsmansStats.get(lineup.get(position)).get("first_player");
                        out += " bowled";
                        out += " " + batsmansStats.get(lineup.get(position)).get("second_player");
                    }else{
                        if(batsmansStats.get(lineup.get(position)).containsKey("first_player")) {
                            out += " " + batsmansStats.get(lineup.get(position)).get("first_player");
                            out += "/";
                        }
                        out += " " + batsmansStats.get(lineup.get(position)).get("second_player");
                    }
                }else{
                    out = batsmansStats.get(lineup.get(position)).get("out");
                    if(out.equals("bowled") || out.equals("run out")) {
                        out += " " + batsmansStats.get(lineup.get(position)).get("first_player");
                    }else if(out.equals("rtd hrt")){
                        out = "rtd hrt";
                    }else{
                        out += " bowled";
                        out += " " + batsmansStats.get(lineup.get(position)).get("first_player");
                    }
                }
                wickets.setText(out);
            }else{
                wickets.setText("Not Out");
            }
            return view;
        }

        public void notifyAdapter(ArrayList<String> lineup, LinkedHashMap<String, HashMap<String, String>> batsmansStats) {
            this.lineup = lineup;
            Log.e("HEreweGo", batsmansStats.toString());
            this.batsmansStats = batsmansStats;
            notifyDataSetChanged();
        }

    }

    class BowlingStatsAdapter extends BaseAdapter{

        ArrayList<String> lineup;
        LinkedHashMap<String, HashMap<String, String>> bowlersStats;

        public BowlingStatsAdapter(ArrayList<String> lineup, LinkedHashMap<String, HashMap<String, String>> bowlersStats) {
            this.lineup = lineup;
            this.bowlersStats = bowlersStats;
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
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.bowling_score_entry, null);
            }
            else {
                view = convertView;
            }
            TextView username = (TextView) view.findViewById(R.id.username);
            username.setText(lineup.get(position));
            TextView overs = (TextView) view.findViewById(R.id.overs);
            overs.setText(bowlersStats.get(lineup.get(position)).get("overs"));
            TextView runs = (TextView) view.findViewById(R.id.runs);
            runs.setText(bowlersStats.get(lineup.get(position)).get("runs"));
            TextView wickets = (TextView) view.findViewById(R.id.wickets);
            wickets.setText(bowlersStats.get(lineup.get(position)).get("wickets"));
            TextView eco = (TextView) view.findViewById(R.id.eco);
            eco.setText(bowlersStats.get(lineup.get(position)).get("eco"));
            return view;
        }

        public void notifyAdapter(ArrayList<String> lineup, LinkedHashMap<String, HashMap<String, String>> bowlersStats) {
            this.lineup = lineup;
            this.bowlersStats = bowlersStats;
            notifyDataSetChanged();
        }

    }

}
