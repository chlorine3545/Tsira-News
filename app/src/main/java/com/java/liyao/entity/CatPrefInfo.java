package com.java.liyao.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CatPrefInfo {
    private String userEmail;
    private List<String> catPref;
    private int catPrefId;

    public int getCatPrefId() {
        return catPrefId;
    }

    public void setCatPrefId(int catPrefId) {
        this.catPrefId = catPrefId;
    }

    public CatPrefInfo(int catPrefId, String userEmail, List<String> catPref) {
        this.userEmail = userEmail;
        this.catPref = catPref;
        this.catPrefId = catPrefId;
    }

    public CatPrefInfo(String userEmail, List<String> catPref) {
        this.userEmail = userEmail;
        this.catPref = catPref;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<String> getCatPref() {
        return catPref;
    }

    public void setCatPref(List<String> catPref) {
        this.catPref = catPref;
    }

    public static List<String> stringToList(String listString) {
        List<String> list = new ArrayList<>();
        if (listString != null && !listString.isEmpty()) {
            listString = listString.substring(1, listString.length() - 1); // 移除首尾的中括号
            StringTokenizer tokenizer = new StringTokenizer(listString, ", "); // 使用逗号和空格作为分隔符
            while (tokenizer.hasMoreTokens()) {
                list.add(tokenizer.nextToken());
            }
        }
        return list;
    }
}
