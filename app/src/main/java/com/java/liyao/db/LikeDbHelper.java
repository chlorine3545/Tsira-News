// Like 和 History 基本上一样，把数据库名什么的改一下就完事，甚至 Info 都可以用同一个

package com.java.liyao.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.java.liyao.entity.HistoryInfo;

import java.util.ArrayList;
import java.util.List;

public class LikeDbHelper extends SQLiteOpenHelper {
    private static LikeDbHelper likeDbHelper;
    private static final String DB_NAME = "like.db";
    private static final int DB_VERSION = 1;

    public LikeDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static LikeDbHelper getInstance(Context context) {
        if (null == likeDbHelper) {
            likeDbHelper = new LikeDbHelper(context, DB_NAME, null, DB_VERSION);
        }
        return likeDbHelper;
    }

    // 历史记录的存储对象应该是新闻对象（DataDTO），但是这是一个极为复杂的类，怎么办呢？

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL("create table like_table(like_id integer primary key autoincrement, " +
                "user_email text," +       // 按照我的规定，我们使用用户邮箱作为用户的唯一标识
                "unique_id text," +
                "news_json text" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 先不写
    }

    public int addLike(String user_email, String unique_id, String news_json) {
        if (!searchLike(unique_id, user_email)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("user_email", user_email);
            values.put("unique_id", unique_id);
            values.put("news_json", news_json);
            String nullColumnHack = "values(null,?,?,?)";

            int insert = (int) db.insert("like_table", nullColumnHack, values);
            db.close();
            // Log.d("SuccessfullyAddToHistory", "onCreate: 已添加到历史记录");
            return insert;
        }
        return 0;
    }

    @SuppressLint("Range")
    public List<HistoryInfo> getLike(String ue) {
        SQLiteDatabase db = getReadableDatabase();
        List<HistoryInfo> list = new ArrayList<>();
        String sql;
        Cursor cursor;
        if (ue == null) {
            sql = "select like_id,user_email,unique_id,news_json  from like_table";
            cursor = db.rawQuery(sql, null);
        } else {
            sql = "select like_id,user_email,unique_id,news_json  from like_table where user_email=?";
            cursor = db.rawQuery(sql, new String[]{ue});
        }
        while (cursor.moveToNext()) {
            int like_id = cursor.getInt(cursor.getColumnIndex("like_id"));
            String user_email = cursor.getString(cursor.getColumnIndex("user_email"));
            String unique_id = cursor.getString(cursor.getColumnIndex("unique_id"));
            String news_json = cursor.getString(cursor.getColumnIndex("news_json"));
            list.add(new HistoryInfo(like_id, user_email, unique_id, news_json));
        }
        cursor.close();
        db.close();
        return list;
    }

    @SuppressLint("Range")
    public boolean searchLike(String uk, String ue) {
        SQLiteDatabase db = getReadableDatabase();
        String sql;
        Cursor cursor;
        if (ue == null) {
            // If user_email is null, modify the query to not include user_email in the WHERE clause
            sql = "select like_id, user_email, unique_id, news_json from like_table where unique_id=?";
            cursor = db.rawQuery(sql, new String[]{uk});
        } else {
            // If user_email is not null, include it in the WHERE clause
            sql = "select like_id, user_email, unique_id, news_json from like_table where unique_id=? and user_email=?";
            cursor = db.rawQuery(sql, new String[]{uk, ue});
        }
        boolean result = cursor.getCount() > 0; // 这个是最可靠的方法，既能正常返回，又能防止内存泄露
        cursor.close();
        db.close();
        return result;
    }

    public int deleteLike(String unique_id, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        // 可能是这里没有考虑邮箱为 null（未登录）的情况，所以没能成功删除
        String whereClause;
        String[] whereArgs;
        if (userEmail == null) {
            whereClause = "unique_id=?";
            whereArgs = new String[]{unique_id};
        } else {
            whereClause = "unique_id=? and user_email=?";
            whereArgs = new String[]{unique_id, userEmail};
        }

        int rowsDeleted = db.delete("like_table", whereClause, whereArgs);
        db.close();
        return rowsDeleted;
    }

    public int deleteAllLike() {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted = db.delete("like_table", null, null);
        db.close();
        return rowsDeleted;
    }
}
