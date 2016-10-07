package com.scorrer.ravi.scorrer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout homeLayout;
    private DrawerLayout drawerLayout;
    private ArrayList<DrawerItem> mDrawerItems = new ArrayList<DrawerItem>();
    private RelativeLayout drawerPane;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Button cricketButton;
    private Button soccerButton;
    private Button tennisButton;
    private Button hockeyButton;
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        homeLayout = (RelativeLayout) drawerLayout.findViewById(R.id.home_layout);
        Toolbar toolbar = (Toolbar) homeLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cricketButton = (Button) homeLayout.findViewById(R.id.cricket_button);
        soccerButton = (Button) homeLayout.findViewById(R.id.soccer_button);
        tennisButton = (Button) homeLayout.findViewById(R.id.tennis_button);
        hockeyButton = (Button) homeLayout.findViewById(R.id.hockey_button);
        pref = getSharedPreferences(getString(R.string.user_data_file), MODE_PRIVATE);

        cricketButton.setOnClickListener(this);
        soccerButton.setOnClickListener(this);
        tennisButton.setOnClickListener(this);
        hockeyButton.setOnClickListener(this);

        drawerPane = (RelativeLayout) drawerLayout.findViewById(R.id.drawerPane);
        RelativeLayout profileBox = (RelativeLayout) drawerPane.findViewById(R.id.profile_box);
        TextView userName = (TextView) profileBox.findViewById(R.id.userName);
        mDrawerList = (ListView) drawerPane.findViewById(R.id.nav_list);
        mDrawerItems.add(new DrawerItem("Challenges", "Accept/Send Challenges"));
        if(pref.getString("status", "null").equals("LoggedIn")){
            mDrawerItems.add(new DrawerItem("LogOut", "Log out of scorrer"));
            userName.setText(pref.getString("name", "Anonymous"));
        }else{
            mDrawerItems.add(new DrawerItem("LogIn", "Log in to scorrer"));
        }
        DrawerListAdapter drawerAdapter = new DrawerListAdapter(this, mDrawerItems);
        mDrawerList.setAdapter(drawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemFromList(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.new_registration){
            Intent i = new Intent(this, RegisterActivity.class);
            startActivity(i);
        }
        return true;
    }

    private void selectedItemFromList(int position){
        String si = mDrawerItems.get(position).title;
        Intent i;
        switch (si){
            case "Challenges":
                i = new Intent(this, ChallengesActivity.class);
                startActivity(i);
                break;
            case "LogIn":
                i = new Intent(this, LogInActivity.class);
                startActivity(i);
                break;
            case "LogOut":
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                i = new Intent(this, HomeActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.cricket_button:
                i = new Intent(this, CricketConfigActivity.class);
                startActivity(i);
                break;
            case R.id.soccer_button:
                i = new Intent(this, SoccerConfigActivity.class);
                startActivity(i);
                break;
            case R.id.tennis_button:
                i = new Intent(this, TennisConfigActivity.class);
                startActivity(i);
                break;
            case R.id.hockey_button:
                i = new Intent(this, HockeyConfigActivity.class);
                startActivity(i);
                break;
        }
    }

    class DrawerItem{
        String title;
        String subTitle;

        public DrawerItem(String title, String subTitle) {
            this.title = title;
            this.subTitle = subTitle;
        }
    }

    class DrawerListAdapter extends BaseAdapter{

        Context context;
        ArrayList<DrawerItem> mDrawerItems;

        public DrawerListAdapter(Context context, ArrayList<DrawerItem> mDrawerItems) {
            this.context = context;
            this.mDrawerItems = mDrawerItems;
        }

        @Override
        public int getCount() {
            return mDrawerItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mDrawerItems.get(position);
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
                view = inflater.inflate(R.layout.drawer_item, null);
            }
            else {
                view = convertView;
            }

            TextView titleView = (TextView) view.findViewById(R.id.title);
            TextView subtitleView = (TextView) view.findViewById(R.id.subTitle);

            titleView.setText( mDrawerItems.get(position).title );
            subtitleView.setText( mDrawerItems.get(position).subTitle );

            return view;
        }
    }
}
