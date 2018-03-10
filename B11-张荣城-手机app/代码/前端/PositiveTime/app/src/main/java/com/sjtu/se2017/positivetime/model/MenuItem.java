package com.sjtu.se2017.positivetime.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MenuItem {
    private int menu_item_icon;
    private String menu_item_name;
    private int backgroundcolor;
    private Class<?> targetActivity;

    public MenuItem(int menu_item_icon, String menu_item_name, int backgroundcolor, Class<?> targetActivity) {
        this.menu_item_icon = menu_item_icon;
        this.menu_item_name = menu_item_name;
        this.backgroundcolor = backgroundcolor;
        this.targetActivity = targetActivity;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(Class<?> targetActivity) {
        this.targetActivity = targetActivity;
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public void setBackgroundcolor(int backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
    }

    public String getMenu_item_name() {
        return menu_item_name;
    }

    public void setMenu_item_name(String menu_item_name) {
        this.menu_item_name = menu_item_name;
    }

    public int getMenu_item_icon() {
        return menu_item_icon;
    }

    public void setMenu_item_icon(int menu_item_icon) {
        this.menu_item_icon = menu_item_icon;
    }

}
