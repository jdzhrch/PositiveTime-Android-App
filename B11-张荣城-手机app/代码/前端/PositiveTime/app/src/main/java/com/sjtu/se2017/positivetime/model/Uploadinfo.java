package com.sjtu.se2017.positivetime.model;

/**
 * Created by bonjour on 17-7-14.
 */

public class Uploadinfo {
    //private UsageStats usageStats;
    private String email;
    private String packageName;
    private String day;
    private int frequency;
    //private long UsedTimebyDay;  //milliseconds
    //private Context context;


    private int duration;
    private int weight;
    private String appname;

    public void setWeight(int weight) { this.weight = weight;}
    public void setEmail(String email){
        this.email = email;
    }
    public void setPackageName(String packageName){
        this.packageName = packageName;
    }
    public void setDay(String day){
        this.day = day;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public void setAppname(String appname) { this.appname = appname; }

    public Uploadinfo(){

    }


    /*public Uploadinfo(UsageStats usageStats , Context context) {
        this.usageStats = usageStats;
        this.context = context;

        try {
            GenerateInfo();
        } catch (PackageManager.NameNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void GenerateInfo() throws PackageManager.NameNotFoundException, NoSuchFieldException, IllegalAccessException {
        PackageManager packageManager = context.getPackageManager();
        this.packageName = usageStats.getPackageName();
        if(this.packageName != null && !this.packageName.equals("")) {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.packageName, 0);
            long UsedTimebyDay = usageStats.getTotalTimeInForeground();
            this.frequency = (Integer) usageStats.getClass().getDeclaredField("mLaunchCount").get(usageStats);
            this.duration = (int) UsedTimebyDay;
            this.email = " ";
            Calendar calendar = Calendar.getInstance();
            this.day = calendar.getTime();

        }
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }


    public int getDuration() {
        return duration;
    }


    public String getPackageName() {
        return packageName;
    }*/
}
