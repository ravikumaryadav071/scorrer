package com.scorrer.ravi.scorrer;

import java.util.HashMap;
import java.util.Map;

public class CreationQuery {
    private static final Map<String, String> query;
    public static final int DATABASE_VERSION = 2;
    static {
        query = new HashMap<String, String>();
        //ec2-52-66-80-107.ap-south-1.compute.amazonaws.com
        query.put("teams", "CREATE TABLE IF NOT EXISTS teams (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, team_name VARCHAR(100) NOT NULL, game_type VARCHAR(20) NOT NULL, date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP);");
        query.put("team_members", "CREATE TABLE IF NOT EXISTS team_members (userid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username VARCHAR(50) NOT NULL);");
        query.put("matches", "CREATE TABLE IF NOT EXISTS matches (match_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, game_type VARCHAR(20) NOT NULL, scorecard VARCHAR(100));");
        //query.put("", "");
    }

    public static String getQuery(String key){
        return query.get(key);
    }
}
