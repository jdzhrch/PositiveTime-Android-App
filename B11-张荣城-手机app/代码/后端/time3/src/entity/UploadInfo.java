package entity;

public class UploadInfo {
    //private UsageStats usageStats;
    public String email;
    public  String packageName;
    public  String day;
    public  int frequency;
    //private long UsedTimebyDay;  //milliseconds
    //private Context context;


    public  int duration;
    public  int weight;
    public  String appname;

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


}

