package com.sjtu.se2017.positivetime.model.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sjtu.se2017.positivetime.model.Statistics.AppInformation;
import com.sjtu.se2017.positivetime.service.util.Utils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/29.
 */

public class ATapplicaion extends Application {
    private static ATapplicaion instance;/*
    static private String email;
    static private long PTime;
    static private long NTime;
    static private int PTotalWeight;
    static private int NTotalWeight;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPTotalWeight() {
        return PTotalWeight;
    }

    public void setPTotalWeight(int PTotalWeight) {
        this.PTotalWeight = PTotalWeight;
    }

    public int getNTotalWeight() {
        return NTotalWeight;
    }

    public void setNTotalWeight(int NTotalWeight) {
        this.NTotalWeight = NTotalWeight;
    }

    public void setPTime(long PTime){
        this.PTime = PTime;
    }

    public void setNTime(long NTime){
        this.NTime = NTime;
    }

    public long getPTime(){
        return PTime;
    }

    public long getNTime(){
        return NTime;
    }

    public long getAT(){
        return (PTime*PTotalWeight - NTime*NTotalWeight)/100;
    }

    public float getPWeight(){
        return PTime/(PTime+NTime);
    }

    public float getNWeight(){
        return NTime/(PTime+NTime);
    }
    */
    public String getEmail() {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getString("email",null);
    }

    public void setEmail(String email) {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("email",email);
        editor.commit();
    }

    public int getPTotalWeight() {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getInt("PTotalWeight",50);
    }

    public void setPTotalWeight(int PTotalWeight) {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("PTotalWeight",PTotalWeight);
        editor.commit();
    }

    public int getNTotalWeight() {
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getInt("NTotalWeight",50);
    }

    public void setNTotalWeight(int NTotalWeight) {
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("NTotalWeight",NTotalWeight);
        editor.commit();
    }

    public void setPTime(long PTime){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putLong("PTime",PTime);
        editor.commit();
    }

    public void setNTime(long NTime){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putLong("NTime",NTime);
        editor.commit();
    }

    public long getPTime(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getLong("PTime",0);
    }

    public long getNTime(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getLong("NTime",0);
    }

    //该值用于每天第一次打开时AT清零
    public int getDay(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getInt("day",0);
    }

    public void setDay(int day){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putInt("day",day);
        editor.commit();
    }

    public long getAT(){
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_YEAR);
        if(getDay()!=day){
            setDay(day);
            Log.v("dayday","dad");
            setPTime(0);
            setNTime(0);
            return 0;
        } else {
            return (getPTime() * getPTotalWeight() - getNTime() * getNTotalWeight()) / 100;
        }
    }

    public float getPWeight(){
        if(getPTime()+getNTime()==0){
            return (float)1/2;
        }else {
            float total = getPTime() * getPTotalWeight() + getNTime() * getNTotalWeight();
            return (getPTime() * getPTotalWeight()) / total;
        }
    }

    public float getNWeight(){
        return 1-getPWeight();
    }

    public void setIfFloatingWindow(boolean IfFloatingWindow){
        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putBoolean("IfFloatingWindow",IfFloatingWindow);
        editor.commit();
    }
    public Boolean getIfFloatingWindow(){
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        return pref.getBoolean("IfFloatingWindow",true);
    }
    static public ATapplicaion getInstance() {
        return instance;
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(this);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //用于叠加计算AT
    static private ArrayList<AppInformation> PreList;
    public void setPreList(ArrayList<AppInformation> PreList) { this.PreList = PreList; }

    public ArrayList<AppInformation> getPreList() { return PreList; }
    //用于截图分享
    private Bitmap mScreenCaptureBitmap;
    private Bitmap pic;
    public void setPic(Bitmap pic){
        this.pic = pic;
    }
    public Bitmap getPic(){
        return pic;
    }
    public Bitmap getmScreenCaptureBitmap() {
        return mScreenCaptureBitmap;
    }
    public void setmScreenCaptureBitmap(Bitmap mScreenCaptureBitmap) {
        this.mScreenCaptureBitmap = mScreenCaptureBitmap;
    }
}