package com.scorrer.ravi.scorrer;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private RelativeLayout registerLayout;
    private EditText username;
    private EditText password;
    private EditText password_repeat;
    private EditText name;
    private EditText email;
    private EditText mobile_no;
    private EditText country;
    private Button submitButton;
    private Map<String, String> data;
    private Handler mainUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerLayout = (RelativeLayout) findViewById(R.id.register_layout);
        Toolbar toolbar = (Toolbar) registerLayout.findViewById(R.id.toolbar);
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

        username = (EditText) registerLayout.findViewById(R.id.username_input);
        password = (EditText) registerLayout.findViewById(R.id.password_input);
        password_repeat = (EditText) registerLayout.findViewById(R.id.password_repeat);
        name = (EditText) registerLayout.findViewById(R.id.name_input);
        email = (EditText) registerLayout.findViewById(R.id.email_input);
        mobile_no = (EditText) registerLayout.findViewById(R.id.mobile_no_input);
        country = (EditText) registerLayout.findViewById(R.id.country_input);
        submitButton = (Button) registerLayout.findViewById(R.id.register_button);
        data = new HashMap<String, String>();
        mainUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                try {
                    JSONObject jsonMsg = new JSONObject(msg.getData().get("msg").toString());
                    String status = jsonMsg.getString("status");
                    Log.e("here", jsonMsg.toString());
                    if(status.equals("error")){
                        showMessages(jsonMsg);
                    }else{
                        Log.e("here", jsonMsg.toString());
                        onSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    new PostData(data, URLs.getURL("Register"), mainUIHandler).post();
                }
            }
        });

    }

    private void onSuccess(){
        Toast.makeText(this, "Your account has been created successfully.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    private void showMessages(JSONObject jsonMsg) throws JSONException{
        new AlertDialog.Builder(this)
                .setTitle("Error Messages")
                .setMessage(jsonMsg.getString("error"))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
    }

    private boolean validate(){
        StringBuilder sb = new StringBuilder();

        String temp = username.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your username.\n");
        }else if(temp.length()<3&&temp.length()>30){
            sb.append("Username must have to be of 3 to 30 characters Long.\n");
        }

        temp = password.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your password.\n");
        }else if(temp.length()<6){
            sb.append("Your password must contain at least 6 characters.\n");
        }

        temp = password_repeat.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Confirem your password.\n");
        }else if(!temp.equals(password.getText().toString().trim())){
            sb.append("Your passwords are not matching.\n");
        }

        temp = name.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your name.\n");
        }else if(temp.length()<1){
            sb.append("Your name must contain at least 2 characters.\n");
        }

        temp = email.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your email address.\n");
        }else if(temp.length()<5){
            sb.append("Your email must contain at least 6 characters.\n");
        }

        temp = mobile_no.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your mobile number.\n");
        }else if(temp.length()<10){
            sb.append("Your mobile number must contain at least 10 digits.\n");
        }

        temp = country.getText().toString().trim();
        if(temp.isEmpty()){
            sb.append("Enter your country.\n");
        }

        if(sb.toString().isEmpty()){
            data.put("username", username.getText().toString().trim());
            data.put("password", password.getText().toString().trim());
            data.put("password_again", password_repeat.getText().toString().trim());
            data.put("name", name.getText().toString().trim());
            data.put("email", email.getText().toString().trim());
            data.put("mobile_no", mobile_no.getText().toString().trim());
            data.put("country", country.getText().toString().trim());
            return true;
        }else{
            new AlertDialog.Builder(this).setTitle("Alert Messages")
                    .setMessage(sb.toString())
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
            return false;
        }
    }
}
