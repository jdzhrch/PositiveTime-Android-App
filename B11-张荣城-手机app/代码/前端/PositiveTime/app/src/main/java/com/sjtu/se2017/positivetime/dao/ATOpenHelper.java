package com.sjtu.se2017.positivetime.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ATOpenHelper extends SQLiteOpenHelper {

    public static final String TABLENAME = "AT";
    public ATOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists "+TABLENAME+"(day INT, hour INT, AT INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
