package com.java.liyao.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import androidx.annotation.Nullable;

import com.java.liyao.entity.UserInfo;

public class UserDbHelper extends SQLiteOpenHelper {
    private static UserDbHelper sHelper;
    private static final String DB_NAME = "users.db";
    private static final int VERSION = 2;

    public UserDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 感谢前人的智慧
    public synchronized static UserDbHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new UserDbHelper(context, DB_NAME, null, VERSION);
        }
        return sHelper;
    }

@Override
public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE user_table(" +
            "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nickname TEXT, " +
            "user_email TEXT, " +
            "password TEXT" +
            ")");
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 检查是否需要进行版本升级
        if (oldVersion < newVersion) {
            // 删除旧的 user_table 表
            db.execSQL("DROP TABLE IF EXISTS user_table");

            // 调用 onCreate 方法来重新创建表
            // 这将使用最新的数据库结构创建 user_table 表
            onCreate(db);
        }
    }

    // 注册功能
    public long register(String nickname, String user_email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("nickname", nickname);
        values.put("user_email", user_email);
        values.put("password", password);

        long insertId = db.insert("user_table", null, values);
        db.close();
        return insertId;
    }

    @SuppressLint("Range")
    public UserInfo login(String user_email) {
        SQLiteDatabase db = getReadableDatabase();
        UserInfo userInfo = null;

        String sql = "select user_id, nickname, user_email, password from user_table where user_email=?";
        String[] selectionArgs = {user_email};

        Cursor cursor = db.rawQuery(sql, selectionArgs);
        if (cursor.moveToNext()) {
            int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
            String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            String email = cursor.getString(cursor.getColumnIndex("user_email"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            userInfo = new UserInfo(user_id, nickname, email, password);
        }
        cursor.close();

        db.close();
        return userInfo;
    }
}
