package com.java.liyao.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.java.liyao.entity.HistoryInfo;

import java.util.ArrayList;
import java.util.List;

public class HistoryDbHelper extends SQLiteOpenHelper {
    private static HistoryDbHelper historyDbHelper;
    private static final String DB_NAME = "history.db";
    private static final int DB_VERSION = 1;

    public HistoryDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static HistoryDbHelper getInstance(Context context) {
        if (null == historyDbHelper) {
            historyDbHelper = new HistoryDbHelper(context, DB_NAME, null, DB_VERSION);
        }
        return historyDbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL("create table history_table(history_id integer primary key autoincrement, " +
                "user_email text," +       // 按照我的规定，我们使用用户邮箱作为用户的唯一标识
                "unique_id text," +
                "news_json text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 先不写
    }

    public int addHistory(String user_email, String unique_id, String news_json) {
        if (!searchHistory(unique_id, user_email)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("user_email", user_email);
            values.put("unique_id", unique_id);
            values.put("news_json", news_json);
            String nullColumnHack = "values(null,?,?,?)";

            int insert = (int) db.insert("history_table", nullColumnHack, values);
            db.close();
            // Log.d("SuccessfullyAddToHistory", "onCreate: 已添加到历史记录");
            return insert;
        }
        return 0;
    }

    // 这个返回的是 HistoryInfo
    @SuppressLint("Range")
    public List<HistoryInfo> getHistory(String ue) {
        SQLiteDatabase db = getReadableDatabase();
        List<HistoryInfo> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (ue == null) {
            sql = "select history_id,user_email,unique_id,news_json  from history_table";
            cursor = db.rawQuery(sql, null);
        } else {
            sql = "select history_id,user_email,unique_id,news_json  from history_table where user_email=?";
            cursor = db.rawQuery(sql, new String[]{ue});
        }
        while (cursor.moveToNext()) {
            int history_id = cursor.getInt(cursor.getColumnIndex("history_id"));
            String user_email = cursor.getString(cursor.getColumnIndex("user_email"));
            String unique_id = cursor.getString(cursor.getColumnIndex("unique_id"));
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            list.add(new HistoryInfo(history_id, user_email, unique_id, news_json));
        }
        cursor.close();
        db.close();
        return list;
    }

    @SuppressLint("Range")
    public boolean searchHistory(String uk, String ue) {
        SQLiteDatabase db = getReadableDatabase();
        String sql;
        Cursor cursor;
        if (ue == null) {
            sql = "select history_id, user_email, unique_id, news_json from history_table where unique_id=?";
            cursor = db.rawQuery(sql, new String[]{uk});
        } else {
            sql = "select history_id, user_email, unique_id, news_json from history_table where unique_id=? and user_email=?";
            cursor = db.rawQuery(sql, new String[]{uk, ue});
        }
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        Log.d("SearchHistory", "searchHistory: unique_id=" + uk + ", user_email=" + ue + ", result=" + result);
        return result;
    }

    public int deleteAllHistory() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete("history_table", null, null);
        db.close();
        return rowsDeleted;
    }
}