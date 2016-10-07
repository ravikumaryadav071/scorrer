package com.scorrer.ravi.scorrer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamsDataEntry {

    private static Handler mainUIHandler;
    private String gameType;
    private Context context;
    private String matchID;
    public TeamsDataEntry(Context context, String gameType, Handler mainUIHandler){
        this.context = context;
        this.gameType = gameType;
        this.mainUIHandler = mainUIHandler;
        enter();
    }

    private void enter(){
        new BackgroundThread().start();
    }

    class BackgroundThread extends Thread{
        @Override
        public void run() {
            Looper.prepare();
            MyDBHandler dbHandler = new MyDBHandler(context, null, null, CreationQuery.DATABASE_VERSION);
            dbHandler.setTable_name("teams");
            HashMap<String, String> data = new HashMap<String, String>();
            ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
            if(gameType.equals("CRICKET")) {
                res = dbHandler.getRow(new String[]{"team_name = \'" + CricketTeamFragment.team + "\'", "game_type = \'" + gameType + "\'"});
            }else if(gameType.equals("HOCKEY")) {
                res = dbHandler.getRow(new String[]{"team_name = \'" + HockeyTeamFragment.team + "\'", "game_type = \'" + gameType + "\'"});
            }else{
                res = dbHandler.getRow(new String[]{"team_name = \'" + SoccerTeamFragment.team + "\'", "game_type = \'" + gameType + "\'"});
            }
            if(res.size()==0) {
                if(gameType.equals("CRICKET")) {
                    data.put("team_name", CricketTeamFragment.team);
                }else if(gameType.equals("HOCKEY")) {
                    data.put("team_name", HockeyTeamFragment.team);
                }else{
                    data.put("team_name", SoccerTeamFragment.team);
                }
                data.put("game_type", gameType);
                dbHandler.addRow(data);
                data.clear();
            }
            if(gameType.equals("CRICKET")) {
                res = dbHandler.getRow(new String[]{"team_name = \'"+CricketTeamFragment.Oteam+"\'", "game_type = \'"+gameType+"\'"});
            }else if(gameType.equals("HOCKEY")) {
                res = dbHandler.getRow(new String[]{"team_name = \'"+HockeyTeamFragment.Oteam+"\'", "game_type = \'"+gameType +"\'"});
            }else{
                res = dbHandler.getRow(new String[]{"team_name = \'"+SoccerTeamFragment.Oteam+"\'", "game_type = \'"+gameType+"\'"});
            }
            if(res.size()==0) {
                if(gameType.equals("CRICKET")) {
                    data.put("team_name", CricketTeamFragment.Oteam);
                }else if(gameType.equals("HOCKEY")) {
                    data.put("team_name", HockeyTeamFragment.team);
                }else{
                    data.put("team_name", SoccerTeamFragment.Oteam);
                }
                data.put("game_type", gameType);
                dbHandler.addRow(data);
                data.clear();
            }
            dbHandler.setTable_name("team_members");
            ArrayList<String> team = CricketTeamFragment.players;
            for(int i=0; i<team.size(); i++){
                res = dbHandler.getRow(new String[]{"username = \'"+team.get(i)+"\'"});
                if(res.size()==0) {
                    data.put("username", team.get(i));
                    dbHandler.addRow(data);
                    data.clear();
                }
            }
            team = CricketTeamFragment.Oplayers;
            for(int i=0; i<team.size(); i++){
                res = dbHandler.getRow(new String[]{"username = \'"+team.get(i)+"\'"});
                if(res.size()==0) {
                    data.put("username", team.get(i));
                    dbHandler.addRow(data);
                    data.clear();
                }
            }
            dbHandler.setTable_name("matches");
            data.put("game_type", gameType);
            dbHandler.addRow(data);
            res = dbHandler.runQuery("SELECT match_id FROM matches WHERE game_type=\'"+gameType+"\' ORDER BY match_id DESC", "SELECT");
            matchID = res.get(0).get("match_id");
            String[] set = new String[2];
            set[0] = "scorecard";
            set[1] = gameType+"_scorecard_"+matchID;
            dbHandler.runQuery("UPDATE matches SET scorecard = \'"+gameType+"_scorecard_"+matchID+"\' WHERE match_id=6 AND game_type=\'"+gameType+"\'", "UPDATE");

            Message msg = mainUIHandler.obtainMessage();
            Bundle reData = new Bundle();
            reData.putString("msg", matchID);
            msg.setData(reData);
            mainUIHandler.sendMessage(msg);
            Looper.loop();
        }
    }

}
