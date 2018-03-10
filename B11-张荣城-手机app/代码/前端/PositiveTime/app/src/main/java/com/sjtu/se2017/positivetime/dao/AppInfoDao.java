package com.sjtu.se2017.positivetime.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sjtu.se2017.positivetime.dao.AppInfoOpenHelper;
import com.sjtu.se2017.positivetime.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppInfoDao {
	private AppInfoOpenHelper openHelper;
	SQLiteDatabase db;

	public AppInfoDao(Context context) {
		openHelper = new AppInfoOpenHelper(context, "app_info", 1);
	}

	public AppInfoDao(){

	}

	/**
	 * 查询数据库有没有对应的数据
	 */
	public Cursor  query(String appName){
		db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from info where label=?",
				new String[] { appName });
		if(cursor!=null){
			cursor.moveToFirst();
		}
		return cursor;
	}

	/*
	 * 插入一条数据，如果数据已经存在，就更新
	 */
	public void insertOrUpdate(String appName, int weight) {
		Cursor cursor = query(appName);

		ContentValues cv = new ContentValues(2);
		cv.put("label",appName);
		cv.put("weight",weight);
		if(cursor.getCount()==0){
			db.insert("info",null,cv);
		}
		else{
			db.update("info",cv,"label=?",new String[]{appName});
		}
		db.close();
	}

	public int checkweight(String appName){
		int weight = 50;
		Cursor cursor = query(appName);
		if(cursor.getCount()==0){
		}
		else{
			weight = cursor.getInt(cursor.getColumnIndex("weight"));
		}
		db.close();
		return weight;
	}

	public String pkNameToLabel(Context context, String packageName){
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = new ApplicationInfo();
		try{
			applicationInfo = packageManager.getApplicationInfo(packageName, 0);
		}catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return (String) packageManager.getApplicationLabel(applicationInfo);
	}
	/*
	 *get all apps installed
	 */
	public List<AppInfo> getAllApps(Context context)throws PackageManager.NameNotFoundException{
		List<AppInfo> appInfos  = new ArrayList<AppInfo>();
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);


		ApplicationInfo ai = packageManager.getApplicationInfo("com.sjtu.se2017.positivetime", 0);

		for (PackageInfo packageInfo : packageInfos) {
			String packageName = packageInfo.packageName;
			//只显示第三方app
            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags)!=0) {
               continue;
            }
			Log.v("demo",packageName+":"+Integer.toHexString(packageInfo.applicationInfo.flags));
			AppInfo appInfo = new AppInfo();
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
			appInfo.setAppName((String) packageManager.getApplicationLabel(applicationInfo));

			if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
				continue;
			}
			appInfo.setImage(applicationInfo.loadIcon(packageManager));
			appInfos.add(appInfo);
		}
		return appInfos;
	}


}
