package com.scorrer.ravi.scorrer;

public class SessionManager {

    private static String session;

    public static String getSession(){
        return session;
    }

    public static void setSession(String ses){
        session = ses;
    }

}