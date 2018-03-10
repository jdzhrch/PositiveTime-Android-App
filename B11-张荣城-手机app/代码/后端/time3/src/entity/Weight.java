package entity;

public class Weight {
    public String email;
    public String packagename;
    public String appname;
    public int weight;
    public int minutes;

    public Weight(String email,String packagename,String appname,int weight,int minutes){
        this.appname=appname;
        this.packagename = packagename;
        this.email=email;
        this.weight=weight;
        this.minutes = minutes;
    }

    public int getWeight(){return weight;};

    public void setWeight(int weight){this.weight = weight;}

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.appname = packagename;
    }

    public void setEmail(String email){this.email = email;}

    public String getEmail(){return email;}
}
