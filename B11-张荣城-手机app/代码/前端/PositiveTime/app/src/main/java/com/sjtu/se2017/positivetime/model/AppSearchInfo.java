package com.sjtu.se2017.positivetime.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/7/12.
 */

public class AppSearchInfo {
    private Drawable image;
    private String appName;
    private String packageName;
    private int weight;
    private int installNum;
    private int minutes;
    private String category;

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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getInstallNum() {
        return installNum;
    }

    public void setInstallNum(int installNum) {
        this.installNum = installNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
