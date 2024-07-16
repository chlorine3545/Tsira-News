package com.java.liyao.entity;

public class UserInfo {
    private int user_id;
    private String nickname;
    private String user_email;
    private String password;

    public static UserInfo sUserInfo;

    public UserInfo(int user_id, String nickname, String user_email, String password) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.user_email = user_email;
        this.password = password;
    }

    public static UserInfo getUserinfo() {
        return sUserInfo;
    }

    public static void setUserinfo(UserInfo userinfo) {
        sUserInfo = userinfo;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
