// 每日数据库（1/114514）

package com.java.liyao.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.java.liyao.entity.AiSummaryInfo;

public class AiSummaryDbHelper extends SQLiteOpenHelper {
    private static AiSummaryDbHelper sHelper;
    private static final String DB_NAME = "summary.db";
    private static final int VERSION = 1;

    public AiSummaryDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public synchronized static AiSummaryDbHelper getInstance(Context context) {
        if (null == sHelper) {
            sHelper = new AiSummaryDbHelper(context, DB_NAME, null, VERSION);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建user_table表
        db.execSQL("create table summary_table(summary_id integer primary key autoincrement, " +
                "unique_id TEXT," +
                "ai_summary TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 先不写
    }

    public int addSummary(String unique_id, String ai_summary) {
        if (!searchSummary(unique_id)) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put("unique_id", unique_id);
            values.put("ai_summary", ai_summary);

            int insert = (int) db.insert("summary_table", null, values);
            db.close();
            return insert;
        }
        return 0;
    }

    @SuppressLint("Range")
    public boolean searchSummary(String uk) {
        SQLiteDatabase db = getReadableDatabase();
        String sql;
        Cursor cursor;
        sql = "select summary_id, unique_id, ai_summary from summary_table where unique_id=?";
        cursor = db.rawQuery(sql, new String[]{uk});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result;
    }

    @SuppressLint("Range")
    public AiSummaryInfo getSummary(String uniqueId) {
        SQLiteDatabase db = this.getReadableDatabase();
        AiSummaryInfo summaryInfo = null;
        String sql = "SELECT summary_id, unique_id, ai_summary FROM summary_table WHERE unique_id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{uniqueId});

        if (cursor.moveToFirst()) {
            int summaryId = cursor.getInt(cursor.getColumnIndex("summary_id"));
            String unique_id = cursor.getString(cursor.getColumnIndex("unique_id"));
            String aiSummary = cursor.getString(cursor.getColumnIndex("ai_summary"));
            summaryInfo = new AiSummaryInfo(0, uniqueId, "");
            summaryInfo.setSummaryId(summaryId);
            summaryInfo.setUniqueId(unique_id);
            summaryInfo.setAiSummary(aiSummary);
        }
        cursor.close();
        db.close();
        return summaryInfo;
    }
}
