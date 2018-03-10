package com.sjtu.se2017.positivetime.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppInfoOpenHelper extends SQLiteOpenHelper {

	public static final String TABLENAME = "info";
	public AppInfoOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists "+TABLENAME+"(label VARCHAR(32) PRIMARY KEY, weight INT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
