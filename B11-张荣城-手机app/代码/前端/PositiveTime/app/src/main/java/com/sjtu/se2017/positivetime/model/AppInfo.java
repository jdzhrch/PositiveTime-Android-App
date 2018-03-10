package com.sjtu.se2017.positivetime.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/6/27.
 */

public class AppInfo {
    private Drawable image;
    private String appName;
    private int weight;

    public AppInfo(Drawable image, String appName, int weight) {
        this.image = image;
        this.appName = appName;
        this.weight = weight;
    }
    public AppInfo() {

    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
/*
    public void updateData(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT * FROM info WHERE label = \""+appName+"\"", null);
        ContentValues cv = new ContentValues(2);
        cv.put("label",appName);
        cv.put("weight",weight);
        if(c.getCount()==0){
            db.insert("info",null,cv);
        }
        else{
            db.update("info",cv,"label=?",new String[]{appName});
        }
        //Log.v("logdemo", db.getPath());
    }

    public void checkWeight(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT * FROM info WHERE label = \""+appName+"\"", null);
        if(c.getCount()==0){
            weight = 50;
        }
        else if(c.moveToFirst()){
            weight = c.getInt(c.getColumnIndex("weight"));
        }
    }*/
}
