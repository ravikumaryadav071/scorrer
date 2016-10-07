package com.scorrer.ravi.scorrer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class PostData {

    private Map<String, String> data;
    private URL url;
    private Handler mainUIHandler;
    private String cookie = "null";
    public PostData(Map<String, String> data, String url, Handler mainUIHandler, String cookie){
        this.cookie = cookie;
        this.data = data;
        try {
            this.url = new URL(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        this.mainUIHandler = mainUIHandler;
    }
    public PostData(Map<String, String> data, String url, Handler mainUIHandler){
        this.data = data;
        try {
            this.url = new URL(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        this.mainUIHandler = mainUIHandler;
    }

    public void post(){
        new BackgroundThread().start();
    }

    class BackgroundThread extends Thread{

        public void run(){
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            Looper.prepare();
            int resCode;
            URLConnection conn = null;
            HttpURLConnection urlConn = null;
            InputStream in = null;
            BufferedReader reader = null;
            StringBuilder resData = null;
            String token = null;
            String response = null;
            if(SessionManager.getSession() == null){
                try {
                    URL sesURL = new URL(URLs.getURL("SetSession"));
                    conn = sesURL.openConnection();

                    if(!(conn instanceof HttpURLConnection)){
                        throw new IOException("URL is not http URL");
                    }
                    urlConn = (HttpURLConnection) conn;
                    urlConn.setRequestMethod("POST");
                    urlConn.setAllowUserInteraction(false);
                    urlConn.setInstanceFollowRedirects(true);
                    urlConn.setReadTimeout(20000);
                    urlConn.setConnectTimeout(30000);
                    urlConn.setDoOutput(true);
                    urlConn.setDoInput(true);
                    if(SessionManager.getSession()!=null) {
                        urlConn.setRequestProperty("Cookie", "session=" + SessionManager.getSession());
                    }
                    urlConn.connect();
                    resCode = urlConn.getResponseCode();
                    if(resCode == HttpURLConnection.HTTP_OK){
                        List<String> mSession =  urlConn.getHeaderFields().get("set-cookie");
                        SessionManager.setSession(HttpCookie.parse(mSession.get(0)).get(0).getValue().toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (urlConn != null) {
                        urlConn.disconnect();
                    }
                }
            }
            try {
                URL sesURL = new URL(URLs.getURL("TokenGenerator"));
                conn = sesURL.openConnection();

                if(!(conn instanceof HttpURLConnection)){
                    throw new IOException("URL is not http URL");
                }
                urlConn = (HttpURLConnection) conn;
                urlConn.setRequestMethod("POST");
                urlConn.setAllowUserInteraction(false);
                urlConn.setInstanceFollowRedirects(true);
                urlConn.setReadTimeout(120000);
                urlConn.setConnectTimeout(120000);
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setRequestProperty("Cookie", "session="+SessionManager.getSession());
                urlConn.connect();
                resCode = urlConn.getResponseCode();
                if(resCode == HttpURLConnection.HTTP_OK){
                    in = urlConn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    String temp;
                    StringBuilder data = new StringBuilder();
                    while((temp=reader.readLine())!=null){
                        data.append(temp);
                    }
                    if(url.toString().equals(URLs.getURL("LogIn"))){
                        List<String> mCookies = urlConn.getHeaderFields().get("set-cookie");
                        if(!mCookies.isEmpty()){
                            SessionManager.setSession(HttpCookie.parse(mCookies.get(0)).get(0).getValue().toString());
                        }
                    }
                    temp = data.toString();
                    if(!temp.isEmpty()){
                        JSONObject jReader = new JSONObject(temp);
                        token = jReader.getString("token");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConn != null) {
                    urlConn.disconnect();
                }
            }

            try {
                StringBuilder postData = new StringBuilder();

                for(Map.Entry<String, String> param: data.entrySet()){
                    if(postData.length() !=0) postData.append("&");
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append("=");
                    postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
                }
                postData.append("&");
                postData.append(URLEncoder.encode("token", "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(token, "UTF-8"));

                conn = url.openConnection();

                if(!(conn instanceof HttpURLConnection)){
                    throw new IOException("URL is not HTTP url.");
                }

                urlConn = (HttpURLConnection) conn;
                urlConn.setRequestMethod("POST");
                urlConn.setAllowUserInteraction(false);
                urlConn.setInstanceFollowRedirects(true);
                urlConn.setReadTimeout(20000);
                urlConn.setConnectTimeout(30000);
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                if(!cookie.equals("null")) {
                    urlConn.setRequestProperty("Cookie", "session=" + SessionManager.getSession() + ";hash=" + cookie);
                }else{
                    urlConn.setRequestProperty("Cookie", "session=" + SessionManager.getSession());
                }

                urlConn.connect();

                byte[] postDataByte = postData.toString().getBytes("UTF-8");
                urlConn.getOutputStream().write(postDataByte);
                urlConn.getOutputStream().flush();

                resCode = urlConn.getResponseCode();
                if(resCode==HttpURLConnection.HTTP_OK){
                    in = urlConn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    String temp;
                    resData = new StringBuilder();
                    while ((temp=reader.readLine())!=null){
                        resData.append(temp);
                    }
                    if(url.toString().equals(URLs.getURL("LogIn"))) {
                        List<String> mCookies = urlConn.getHeaderFields().get("set-cookie");
                        if (mCookies != null) {
                            if (!mCookies.isEmpty()) {
                                try {
                                    JSONObject jsonReader = new JSONObject(resData.toString());
                                    jsonReader.put("cookie", HttpCookie.parse(mCookies.get(1)).get(0).getValue().toString());
                                    response = jsonReader.toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (urlConn != null) {
                    urlConn.disconnect();
                }
            }

            if (mainUIHandler != null) {
                Message msg = mainUIHandler.obtainMessage();
                Bundle bundle = new Bundle();
                if(response!=null){
                    bundle.putString("msg", response);
                }else {
                    bundle.putString("msg", resData.toString());
                    Log.e("postata", resData.toString());
                }
                msg.setData(bundle);
                mainUIHandler.sendMessage(msg);
            }

            Looper.loop();

        }

    }

}