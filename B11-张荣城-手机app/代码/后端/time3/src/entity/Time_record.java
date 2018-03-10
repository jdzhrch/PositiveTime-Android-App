package entity;


public class Time_record {

    private String email;
    private String packageName;
    private String appname;
    private int duration;

    public Time_record(String e,String p,String a,int duration){

        this.duration =duration;
        this.email = e;
        this.appname = a;
        this.packageName = p;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPackageName(String packageName){
        this.packageName = packageName;
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
    public int getDuration(){
        return this.duration;
    }
    public String getAppname(){return appname;}
    public void setAppname(String a){this.appname = a ;}

}
