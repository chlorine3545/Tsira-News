package com.java.liyao.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.java.liyao.entity.CatPrefInfo;

import java.util.ArrayList;
import java.util.List;

public class CatPrefDbHelper extends SQLiteOpenHelper {
    private static CatPrefDbHelper sHelper;
    private static final String DB_NAME = "catpref.db";
    private static final int VERSION = 1;

    public CatPrefDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static CatPrefDbHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new CatPrefDbHelper(context, DB_NAME, null, VERSION);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建user_table表
        db.execSQL("create table catpref_table(catpref_id integer primary key autoincrement, " +
                "user_email text," +       // 按照我的规定，我们使用用户邮箱作为用户的唯一标识
                "cat_pref text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 先不写
    }

    public int addCatPref(String user_email, String cat_pref) {
        if (!searchCatPref(user_email)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("user_email", user_email);
            values.put("cat_pref", cat_pref);

            int insert = (int) db.insert("catpref_table", null, values);
            db.close();
            return insert;
        }
        return 0;
    }

    @SuppressLint("Range")
    public List<CatPrefInfo> getCatPref(String ue) {
        SQLiteDatabase db = getReadableDatabase();
        List<CatPrefInfo> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (ue == null) {
            sql = "select catpref_id,user_email,cat_pref  from catpref_table";
            cursor = db.rawQuery(sql, null);
        } else {
            sql = "select catpref_id,user_email,cat_pref  from catpref_table where user_email=?";
            cursor = db.rawQuery(sql, new String[]{ue});
        }
        while (cursor.moveToNext()) {
            int catpref_id = cursor.getInt(cursor.getColumnIndex("catpref_id")); // Corrected column name
            String user_email = cursor.getString(cursor.getColumnIndex("user_email"));
            String cat_pref = cursor.getString(cursor.getColumnIndex("cat_pref"));
            List<String> cat_prefList = CatPrefInfo.stringToList(cat_pref);
            list.add(new CatPrefInfo(catpref_id, user_email, cat_prefList));
        }
        cursor.close();
        db.close();
        return list;
    }

    @SuppressLint("Range")
    public boolean searchCatPref(String ue) {
        SQLiteDatabase db = getReadableDatabase();
        String sql;
        Cursor cursor;
        if (ue == null) {
            // If user_email is null, modify the query to not include user_email in the WHERE clause
            sql = "select catpref_id, user_email, cat_pref from catpref_table";
            cursor = db.rawQuery(sql, null);
        } else {
            // If user_email is not null, include it in the WHERE clause
            sql = "select catpref_id, user_email, cat_pref from catpref_table where user_email=?";
            cursor = db.rawQuery(sql, new String[]{ue});
        }
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    // 还需要一个修改列表的函数，这里我们假定邮箱都是已经存在的
    public int updateCatPref(String user_email, List<String> cat_pref) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String cat_pref_string = cat_pref.toString();
        values.put("cat_pref", cat_pref_string);
        String whereClause = "user_email=?";
        String[] whereArgs = {user_email};
        int update = db.update("catpref_table", values, whereClause, whereArgs);
        db.close();
        return update;
    }

    // 再写一个获取分类偏好的方法
    public List<String> getCatPrefList(String user_email) {
        List<CatPrefInfo> catPref = getCatPref(user_email);
        if (catPref.isEmpty()) {
            return null;
        }
        return catPref.get(0).getCatPref();
    }
}
