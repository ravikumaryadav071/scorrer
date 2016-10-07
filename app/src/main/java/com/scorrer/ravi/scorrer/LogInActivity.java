package com.scorrer.ravi.scorrer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class LogInActivity extends AppCompatActivity {

    private RelativeLayout logInLayout;
    private EditText username;
    private EditText password;
    private Button logInButton;
    private SharedPreferences prefs;
    private Handler mainUIHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInLayout = (RelativeLayout) findViewById(R.id.log_in_layout);

        Toolbar toolbar = (Toolbar) logInLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();
            }
        });

        username = (EditText) logInLayout.findViewById(R.id.username);
        password = (EditText) logInLayout.findViewById(R.id.password);
        logInButton = (Button) logInLayout.findViewById(R.id.log_in_button);
        prefs = getSharedPreferences(getString(R.string.user_data_file), MODE_PRIVATE);

        mainUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent i;
                try {
                    JSONObject jsonMsg = new JSONObject(msg.getData().getString("msg"));
                    String status = jsonMsg.getString("status");
                    if(status.equals("LoggedIn")){
                        JSONObject userInfo = jsonMsg.getJSONObject("user_info");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("status", "LoggedIn");
                        editor.putString("username", username.getText().toString().trim());
                        editor.putString("name", userInfo.getString("name"));
                        editor.putString("userid", userInfo.getString("userid"));
                        editor.putString("cookie", jsonMsg.getString("cookie"));
                        editor.commit();
                        i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                        finish();
                    }else if(status.equals("LogInFailed")){
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Error Message")
                                .setMessage(jsonMsg.getString("error"))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("username", username.getText().toString().trim());
                    data.put("password", password.getText().toString().trim());
                    data.put("remember", "on");
                    new PostData(data, URLs.getURL("LogIn"), mainUIHandler).post();
                }
            }
        });

    }

    private boolean validate(){
        if(username.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), "Enter your username.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText().toString().trim().equals("")){
            Toast.makeText(getApplicationContext(), "Enter your password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
