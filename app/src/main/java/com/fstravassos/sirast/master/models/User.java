package com.fstravassos.sirast.master.models;

/**
 * Created by Felipe on 12/06/2017.
 */

public class User {
    private String mNumber;
    private String mUserName;
    private String mPassword;

    public User(String mNumber, String mUserName, String mPassword) {
        this.mNumber = mNumber;
        this.mUserName = mUserName;
        this.mPassword = mPassword;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
