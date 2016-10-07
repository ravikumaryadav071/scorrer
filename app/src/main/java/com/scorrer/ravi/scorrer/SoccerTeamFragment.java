package com.scorrer.ravi.scorrer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SoccerTeamFragment extends Fragment {

    public interface FragmentCallBackListener{
        public void onCallBack(HashMap<String, Object> msg, int pageNo);
    }

    private Handler mainUIHandler;
    private static final String ARG_PAGE = "page_no";
    private int page_no;
    private FragmentCallBackListener listener;
    private SharedPreferences prefs;
    public static final ArrayList<String> Oplayers = new ArrayList<String>();
    public static final ArrayList<String> players = new ArrayList<String>();
    public static String team = "";
    public static String Oteam = "";
    public static String time = "";
    public static Button nextButton;
    public static String toss;
    public static final ArrayList<String> currentBatsmen = new ArrayList<String>();
    public static final ArrayList<String> currentBowlers = new ArrayList<String>();

    public SoccerTeamFragment() {

    }

    public static SoccerTeamFragment newInstance(int page, String data, Button next) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        if(page==2) {
            args.putString("team1", data);
        }else if(page==3){
            args.putString("team1", data);
        }else if(page==4){
            args.putString("team2", data);
        }
        SoccerTeamFragment fragment = new SoccerTeamFragment();
        fragment.setArguments(args);
        nextButton = next;
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (FragmentCallBackListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page_no = getArguments().getInt(ARG_PAGE);
        prefs = getContext().getSharedPreferences(getString(R.string.user_data_file), Context.MODE_PRIVATE);
        mainUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.e("cricketTeamFragment", msg.getData().toString());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=null;
        final MyDBHandler dbHandler = new MyDBHandler(getContext(), null, null, CreationQuery.DATABASE_VERSION);
        switch (page_no){
            case 1:
                view = inflater.inflate(R.layout.fragment_cricket_team_name, container, false);
                Button addTeam = (Button) view.findViewById(R.id.add_team_button);
                final View finalView = view;
                final ArrayList<String> teams = new ArrayList<String>();
                final AutoCompleteTextView teamName = (AutoCompleteTextView) finalView.findViewById(R.id.team_name);
                teamName.setText(team);
                final SuggAdapter adapter = new SuggAdapter(getContext(), teams);
                final int[] c = {1};

                teamName.setThreshold(1);
                adapter.setNotifyOnChange(true);
                teamName.setAdapter(adapter);
                teamName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().equals("")) {
                            dbHandler.setTable_name("teams");
                            ArrayList<HashMap<String, String>> results = dbHandler.runQuery("SELECT * FROM teams WHERE game_type=\'SOCCER\' AND team_name LIKE \'%" + s.toString() + "%\' ", "SELECT");
                            int tot = 0;
                            if (results != null) {
                                tot = results.size();
                            }
                            if (tot > 0) {
                                teams.clear();
                                adapter.clear();
                            }
                            for (int i = 0; i < tot; i++) {
                                HashMap<String, String> result = results.get(i);
                                teams.add(result.get("team_name"));
                            }
                            //adapter.setList(teams);
                            if(c[0] ==1){
                                c[0]++;
                            }else {
                                adapter.addAll(teams);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                addTeam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(teamName.getText().toString().trim().equals("")){
                            Toast.makeText(getContext(), "Enter your Team's Name.", Toast.LENGTH_LONG).show();
                        }else{
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("team1_name", teamName.getText().toString().trim());
                            listener.onCallBack(data, 1);
                            team = teamName.getText().toString().trim();
                        }
                    }
                });
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(teamName.getText().toString().trim().equals("")){
                            Toast.makeText(getContext(), "Enter your Team's Name.", Toast.LENGTH_LONG).show();
                        }else{
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("team1_name", teamName.getText().toString().trim());
                            listener.onCallBack(data, 1);
                            team = teamName.getText().toString().trim();
                        }
                    }
                });;
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_cricket_team, container, false);
                String team_name = getArguments().getString("team1");
                RelativeLayout teamML = (RelativeLayout) view.findViewById(R.id.team_members_layout);
                TextView teamNT = (TextView) teamML.findViewById(R.id.team_name_title);
                teamNT.setText(team_name);
                Button addTM = (Button) teamML.findViewById(R.id.add_team_mate);

                final int[] c2 = {1};

                ListView playersL = (ListView) teamML.findViewById(R.id.players_list);
                final PlayerListAdapter playerLA = new PlayerListAdapter(getContext(), players);
                playersL.setAdapter(playerLA);

                final ArrayList<String> suggPlayers = new ArrayList<String>();
                final AutoCompleteTextView teamMate = (AutoCompleteTextView) teamML.findViewById(R.id.team_mate);
                final TeamMateSugg tAdapter = new TeamMateSugg(getContext(), suggPlayers);
                teamMate.setThreshold(1);
                teamMate.setAdapter(tAdapter);
                teamMate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        dbHandler.setTable_name("teams");
                        ArrayList<HashMap<String, String>> results = dbHandler.runQuery("SELECT * FROM team_members WHERE username LIKE \'%"+s.toString()+"%\' ", "SELECT");
                        int tot = 0;
                        if(results!=null){
                            tot = results.size();
                        }
                        if(tot>0){
                            suggPlayers.clear();
                            tAdapter.clear();
                        }
                        for(int i=0; i<tot; i++){
                            HashMap<String, String> result = results.get(i);
                            if(!Oplayers.contains(result.get("username"))) {
                                if(!players.contains(result.get("username"))) {
                                    suggPlayers.add(result.get("username"));
                                }
                            }
                        }
                        if(c2[0] ==1){
                            c2[0]++;
                        }else {
                            tAdapter.addAll(suggPlayers);
                        }
                        tAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                addTM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(players.size()<11) {
                            if(!teamMate.getText().toString().trim().equals("")) {
                                if (!Oplayers.contains(teamMate.getText().toString().trim())) {
                                    if (!players.contains(teamMate.getText().toString().trim())) {
                                        players.add(teamMate.getText().toString().trim());
                                        teamMate.setText("");
                                        playerLA.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getContext(), "This player is already added in your team.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "This player is already added in opposite team.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getContext(), "Enter player's name.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Cannot add more than 11 players.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(players.size()<2){
                            Toast.makeText(getContext(), "Add at least 2 Team Members.", Toast.LENGTH_LONG).show();
                        }else{
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("team1", players);
                            listener.onCallBack(data, 2);
                        }
                    }
                });

                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_opposite_cricket_team_name, container, false);
                final String team1_name = getArguments().getString("team1");
                Button addOTeam = (Button) view.findViewById(R.id.add_team_button);
                final View finalView1 = view;
                final ArrayList<String> Oteams = new ArrayList<String>();
                final AutoCompleteTextView OteamName = (AutoCompleteTextView) finalView1.findViewById(R.id.team_name);

                final int c3[] = {1};

                OteamName.setText(Oteam);
                final SuggAdapter Oadapter = new SuggAdapter(getContext(), Oteams);
                OteamName.setThreshold(1);
                OteamName.setAdapter(Oadapter);
                OteamName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        dbHandler.setTable_name("teams");
                        ArrayList<HashMap<String, String>> results = dbHandler.runQuery("SELECT * FROM teams WHERE game_type=\'SOCCER\' AND team_name != \'"+team1_name+"\' AND team_name LIKE \'%"+s.toString()+"%\' ", "SELECT");
                        int tot = 0;
                        if(results!=null){
                            tot = results.size();
                            Oadapter.clear();
                        }
                        if(tot>0){
                            Oteams.clear();
                        }
                        for(int i=0; i<tot; i++){
                            HashMap<String, String> result = results.get(i);
                            Oteams.add(result.get("team_name"));
                        }
                        if(c3[0] ==1){
                            c3[0]++;
                        }else {
                            Oadapter.addAll(Oteams);
                        }
                        Oadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                addOTeam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(OteamName.getText().toString().trim().equals("")){
                            Toast.makeText(getContext(), "Enter your Team's Name.", Toast.LENGTH_LONG).show();
                        }else{
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("team2_name", OteamName.getText().toString().trim());
                            listener.onCallBack(data, 3);
                            Oteam = OteamName.getText().toString().trim();
                        }
                    }
                });
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(OteamName.getText().toString().trim().equals("")){
                            Toast.makeText(getContext(), "Enter your Team's Name.", Toast.LENGTH_LONG).show();
                        }else{
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("team2_name", OteamName.getText().toString().trim());
                            listener.onCallBack(data, 3);
                            Oteam = OteamName.getText().toString().trim();
                        }
                    }
                });;
                break;
            case 4:
                view = inflater.inflate(R.layout.fragment_opposite_cricket_team, container, false);
                String Oteam_name = getArguments().getString("team2");
                RelativeLayout OteamML = (RelativeLayout) view.findViewById(R.id.team_members_layout);
                TextView OteamNT = (TextView) OteamML.findViewById(R.id.team_name_title);
                OteamNT.setText(Oteam_name);
                Button OaddTM = (Button) OteamML.findViewById(R.id.add_team_mate);
                //Button nextSlide = (Button) ;
                final int[] c4 = {1};
                ListView OplayersL = (ListView) OteamML.findViewById(R.id.players_list);
                final PlayerListAdapter OplayerLA = new PlayerListAdapter(getContext(), Oplayers);
                OplayersL.setAdapter(OplayerLA);

                final ArrayList<String> OsuggPlayers = new ArrayList<String>();
                final AutoCompleteTextView OteamMate = (AutoCompleteTextView) OteamML.findViewById(R.id.team_mate);
                OteamMate.setHint(getString(R.string.opposite_team_mate_hint));
                final TeamMateSugg OtAdapter = new TeamMateSugg(getContext(), OsuggPlayers);
                OteamMate.setThreshold(1);
                OteamMate.setAdapter(OtAdapter);
                OteamMate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        dbHandler.setTable_name("teams");
                        ArrayList<HashMap<String, String>> results = dbHandler.runQuery("SELECT * FROM team_members WHERE username LIKE \'%"+s.toString()+"%\' ", "SELECT");
                        int tot = 0;
                        if(results!=null){
                            tot = results.size();
                        }
                        if(tot>0){
                            OsuggPlayers.clear();
                            OtAdapter.clear();
                        }
                        for(int i=0; i<tot; i++){
                            HashMap<String, String> result = results.get(i);
                            if(!players.contains(result.get("username"))) {
                                if(!Oplayers.contains(result.get("username"))) {
                                    OsuggPlayers.add(result.get("username"));
                                }
                            }
                        }
                        if(c4[0] ==1){
                            c4[0]++;
                        }else {
                            OtAdapter.addAll(OsuggPlayers);
                        }
                        OtAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                OaddTM.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Oplayers.size()<11) {
                            if(!OteamMate.getText().toString().trim().equals("")) {
                                if (!players.contains(OteamMate.getText().toString().trim())) {
                                    if (!Oplayers.contains(OteamMate.getText().toString().trim())) {
                                        Oplayers.add(OteamMate.getText().toString().trim());
                                        OteamMate.setText("");
                                        OplayerLA.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getContext(), "This player is already added in this team.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "This player is already added in your team.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getContext(), "Enter player's name.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getContext(), "Cannot add more than 11 players.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Oplayers.size()<2){
                            Toast.makeText(getContext(), "Add at least 2 Team Members.", Toast.LENGTH_LONG).show();
                        }else{
                            if(players.size()==Oplayers.size()) {
                                HashMap<String, Object> data = new HashMap<String, Object>();
                                data.put("team2", Oplayers);
                                listener.onCallBack(data, 4);
                            }else{
                                Toast.makeText(getContext(), "Team size must be equal.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                break;
            case 5:
                view = inflater.inflate(R.layout.fragment_time, container, false);
                final EditText timeT = (EditText) view.findViewById(R.id.time);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(timeT.getText().toString().trim().equals("")){
                            Toast.makeText(getContext(), "Add at least 1 over.", Toast.LENGTH_LONG).show();
                        }else{
                            time = timeT.getText().toString().trim();
                            HashMap<String, Object> data = new HashMap<String, Object>();
                            data.put("time", time);

                            listener.onCallBack(data, 5);
                        }
                    }
                });
                break;
        }
        return view;
    }

    class TeamMateSugg extends ArrayAdapter<String>{
        private Context context;
        private ArrayList<String> team_mates;

        public TeamMateSugg(Context context, ArrayList<String> teams){
            super(context, R.layout.team_name_sugg, teams);
            this.context = context;
            this.team_mates = teams;
        }

        @Override
        public int getCount() {
            return team_mates.size();
        }

        @Override
        public String getItem(int position) {
            return team_mates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.team_mates_sugg, null);
            }
            else {
                view = convertView;
            }
            LinearLayout teamMS = (LinearLayout) view.findViewById(R.id.team_mates_sugg);
            TextView tm = (TextView) teamMS.findViewById(R.id.team_mate);
            tm.setText(team_mates.get(position));
            return view;
        }
    }

    class SuggAdapter extends ArrayAdapter<String>{

        private Context context;
        private ArrayList<String> teams;

        public SuggAdapter(Context context, ArrayList<String> teams){
            super(context, R.layout.team_name_sugg, teams);
            this.context = context;
            this.teams = teams;
        }

        public void setList(ArrayList<String> teams){
            this.teams = teams;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return teams.size();
        }

        @Override
        public String getItem(int position) {
            return teams.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.team_name_sugg, null);
            }
            else {
                view = convertView;
            }
            LinearLayout teamNS = (LinearLayout) view.findViewById(R.id.team_name_sugg_layout);
            TextView sugg = (TextView) teamNS.findViewById(R.id.sugg_list_item);
            sugg.setText(teams.get(position));
            return view;
        }
    }

    class PlayerListAdapter extends BaseAdapter{

        Context context;
        ArrayList<String> players;

        public PlayerListAdapter(Context context, ArrayList<String> players){
            this.context = context;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.player_list_entry, null);
            }
            else {
                view = convertView;
            }
            RelativeLayout playerLEL = (RelativeLayout) view.findViewById(R.id.player_list_entry_layout);
            final TextView playerName = (TextView) playerLEL.findViewById(R.id.player_username);
            playerName.setText(players.get(position));
            ImageButton edit = (ImageButton) playerLEL.findViewById(R.id.edit_button);
            ImageButton cancel = (ImageButton) playerLEL.findViewById(R.id.cancel_button);

            final EditText input = new EditText(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            input.setText(players.get(position));
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Edit")
                            .setMessage("Edit Name")
                            .setIcon(R.drawable.edit_icon)
                            .setView(input)
                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    players.set(position, input.getText().toString().trim());
                                    PlayerListAdapter.this.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    players.remove(position);
                    PlayerListAdapter.this.notifyDataSetChanged();
                }
            });
            return view;
        }
    }
}
