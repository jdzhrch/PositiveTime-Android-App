package entity;

import java.util.Date;
public class Record {
    private String email;
    private String packageName;
    private Date day;
    private int frequency;
    //private long UsedTimebyDay;  //milliseconds
    //private Context context;
    private int duration;

    public Record(String e,String p,Date d,int f,int duration){
        this.day =d;
        this.duration =duration;
        this.email = e;
        this.frequency = f;
        this.packageName = p;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPackageName(String packageName){
        this.packageName = packageName;
    }
    public void setDay(Date day){
        this.day = day;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPackageName(){
        return this.packageName;
    }
    public Date getDay(){
        return this.day;
    }
    public int getFrequency(){
        return this.frequency;
    }
    public int getDuration(){
        return this.duration;
    }
}
