package com.sjtu.se2017.positivetime.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jn.chart.data.Entry;

import java.util.ArrayList;

public class ATDao {
    private ATOpenHelper openHelper;
    SQLiteDatabase db;

    public ATDao(Context context) {
        openHelper = new ATOpenHelper(context, "AT_info", 1);
    }

    /**
     * 查询数据库有没有对应的数据
     */
    public Cursor  query(int day,int hour){
        db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from AT where day=? and hour=?",
                new String[] { ""+day,""+hour });
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    /*
     * 插入一条数据，如果数据已经存在，就更新
     */
    public void insertOrUpdate(int day,int hour, long AT) {
        Cursor cursor = query(day,hour);

        ContentValues cv = new ContentValues(2);
        cv.put("day",day);
        cv.put("hour",hour);
        cv.put("AT",AT);
        if(cursor.getCount()==0){
            db.insert("AT",null,cv);
        }
        else{
            db.update("AT",cv,"day=? and hour=?",new String[]{""+day,""+hour});
        }
        db.close();
    }

    public int checkAT(int day,int hour){
        int AT = 0;
        Cursor cursor = query(day,hour);
        if(cursor.getCount()==0){
            Log.v("checkAT","this hour has not AT");
        }
        else{
            AT = cursor.getInt(cursor.getColumnIndex("AT"));
        }
        db.close();
        return AT;
    }

    public ArrayList<Entry> checkATofToday(int day){
        db = openHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from AT where day=?",
                new String[] { ""+day });
        if(cursor!=null){
            cursor.moveToFirst();
        }

        ArrayList<Entry> arrayList = new ArrayList<Entry>();
        while (cursor.moveToNext()) {
            int hour = cursor.getInt(cursor.getColumnIndex("hour"));
            int AT = cursor.getInt(cursor.getColumnIndex("AT"));
            //Log.v("checkATofToday",hour+";"+AT);
            arrayList.add(new Entry(AT,hour));
        }
        db.close();
        return arrayList;
    }
}
