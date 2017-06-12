package com.fstravassos.sirast.master;

/**
 * Created by Felipe on 05/06/2017.
 */

public class Session {

    public static String mUser;
    public static String mNumber;
    public static String mId;

    public static void login(String user, String number, String id) {
        mUser = user;
        mNumber = number;
        mId = id;
    }



}
