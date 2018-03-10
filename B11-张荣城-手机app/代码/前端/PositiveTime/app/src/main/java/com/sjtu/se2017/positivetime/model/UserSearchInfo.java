package com.sjtu.se2017.positivetime.model;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class UserSearchInfo {
    private Drawable avatar;
    private String email;
    private String username;
    private List<Integer> achievements;

    public List<Integer> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Integer> achievements) {
        this.achievements = achievements;
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String achievementsToString(){
        String str="";
        for(int i=0;i < achievements.size();i++){
            str += achievements.get(i);
        }
        return str;
    }
}
