package com.java.liyao.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserInfo {
    private int user_id;
    private String nickname;
    private String user_email;
    private String password;

    public static UserInfo sUserInfo; // 没登录的时候就是 null

    public List<String> categories = new ArrayList<>(Arrays.asList(new String[]{"全部", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"}));
    // 设置成公开的有点不好，但是为了方便，就这样了。

    public UserInfo(int user_id, String nickname, String user_email, String password) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.user_email = user_email;
        this.password = password;
    }

    public List<String> getCategories() {
        return categories;
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
