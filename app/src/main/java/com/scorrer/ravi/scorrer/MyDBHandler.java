package com.scorrer.ravi.scorrer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyDBHandler extends SQLiteOpenHelper{

    private String table_name;
    private static String database = "scorrer.db";
    private String query;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, database, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query = CreationQuery.getQuery("teams");
        db.execSQL(query);
        query = CreationQuery.getQuery("team_members");
        db.execSQL(query);
        query = CreationQuery.getQuery("matches");
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        query = "DROP TABLE IF EXISTS teams;";
        db.execSQL(query);
        query = CreationQuery.getQuery("teams");
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS team_members;";
        db.execSQL(query);
        query = CreationQuery.getQuery("team_members");
        db.execSQL(query);
        query = "DROP TABLE IF EXISTS matches;";
        db.execSQL(query);
        query = CreationQuery.getQuery("matches");
        db.execSQL(query);
    }

    public void setTable_name(String table_name){
        this.table_name = table_name;
    }

    public void addRow(Map<String, String> entries){
        ContentValues values = new ContentValues();
        for(Map.Entry<String, String> entry: entries.entrySet()){
            values.put(entry.getKey(), entry.getValue());
        }
        SQLiteDatabase db = getWritableDatabase();
        db.insert(table_name, null, values);
        db.close();
    }

    public ArrayList<HashMap<String, String>> getRow(String[] conditions){
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        int len = conditions.length;
        String constr = "";
        for(int i=0; i<len; i++){
            if(i==0) {
                constr += conditions[i];
            }else{
                constr += " AND "+conditions[i]+" ";
            }
        }
        SQLiteDatabase db = getWritableDatabase();
        this.query = "SELECT * FROM "+table_name+" WHERE "+constr+";";
        Cursor c = db.rawQuery(this.query, null);
        c.moveToFirst();
        while (!c.isAfterLast()){
            HashMap<String, String> entry = new HashMap<String, String>();
            for(int i=0; i<c.getColumnCount(); i++){
                entry.put(c.getColumnName(i), c.getString(i));
            }
            result.add(entry);
            c.moveToNext();
        }
        db.close();
        c.close();
        return result;
    }

    public ArrayList<HashMap<String, String>> runQuery(String query, String type){
        SQLiteDatabase db = getWritableDatabase();
        if(type.equals("SELECT")){
            Cursor c = db.rawQuery(query, null);
            if(c.getCount()>0) {
                ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    HashMap<String, String> entry = new HashMap<String, String>();
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        entry.put(c.getColumnName(i), c.getString(i));
                    }
                    result.add(entry);
                    c.moveToNext();
                }
                c.close();
                db.close();
                return result;
            }else{
                db.close();
                return null;
            }
        }else{
            db.execSQL(query);
            db.close();
            return null;
        }
    }

    public void updateTable(String[] update, String[] conditions){
        SQLiteDatabase db = getWritableDatabase();
        int len = conditions.length;
        String constr = "";
        for(int i=0; i<len; i++){
            if(i==0) {
                constr += conditions[i];
            }else{
                constr += " AND "+conditions[i]+" ";
            }
        }
        this.query = "UPDATE "+this.table_name+" SET "+update[0]+" = "+update[1]+" WHERE "+constr+";";
        //Log.e("query", this.query);
        db.execSQL(this.query);
        db.close();
    }

    public void deleteRow(String[] conditions){
        SQLiteDatabase db = getWritableDatabase();
        int len = conditions.length;
        String constr = "";
        for(int i=0; i<len; i++){
            if(i==0) {
                constr += conditions[i];
            }else{
                constr += " AND "+conditions[i]+" ";
            }
        }
        this.query = "DELETE FROM "+this.table_name+" WHERE "+constr+";";
        db.execSQL(this.query);
        db.close();
    }

}