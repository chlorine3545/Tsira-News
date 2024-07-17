package com.java.liyao.entity;

public class HistoryInfo {
    private int history_id;
    private String user_email;
    private String unique_id;
    private String news_json;

    public HistoryInfo(int history_id, String user_email, String unique_id, String news_json) {
        this.history_id = history_id;
        this.user_email = user_email;
        this.unique_id = unique_id;
        this.news_json = news_json;
    }

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getNews_json() {
        return news_json;
    }

    public void setNews_json(String news_json) {
        this.news_json = news_json;
    }
}
