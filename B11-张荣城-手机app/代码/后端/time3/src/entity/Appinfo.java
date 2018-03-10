package entity;

public class Appinfo {
    public String packageName;
    public String appName;
    public int category;
    public int weight;
    public int installNum;
    public int minutes;
    public String image;//存图片在服务器上url
    public Appinfo() {
    }

    public Appinfo(String packagename,String appname, int category,int weight,String image) {
        this.appName = appname;
        this.category = category;
        this.weight = weight;
        this.installNum =1;
        this.packageName = packagename;
        this.image = image;
        this.minutes = 0;
    }
    public Appinfo(String packagename,String appname, int category,int weight,String image,int installnum) {
        this.appName = appname;
        this.category = category;
        this.weight = weight;
        this.installNum = installnum;
        this.packageName = packagename;
        this.image = image;
        this.minutes = 0;
    }
    public Appinfo(String packagename,String appname, int category,int weight,String image,int installnum,int minutes) {
        this.appName = appname;
        this.category = category;
        this.weight = weight;
        this.installNum = installnum;
        this.packageName = packagename;
        this.image = image;
        this.minutes = minutes;
    }
    public void print(){
        System.out.println(this.appName);
        System.out.println(this.packageName);
        System.out.println(this.installNum);
        System.out.println(this.category);
        System.out.println(this.weight);
        System.out.println(this.image);
    }
    public int getWeight(){return weight;};

    public void setWeight(int weight){this.weight = weight;}

    public int getIntallNum(){return installNum;};

    public void setIntallNum(int i){this.installNum = i;}

    public String getAppname() {
        return appName;
    }

    public void setAppname(String appname) {
        this.appName = appname;
    }

    public String getPackagename() {
        return packageName;
    }

    public void setPackagename(String packagename) {
        this.appName = packagename;
    }

    public int getCategory(){return category;}

    public void setCategory(int category){this.category = category;}

    public void setMinutes(int minutes){this.minutes = minutes;}

    public int getMinutes(){return minutes;}

    public String getImage(){return image;}

    public void setIamge(String i){this.image = i;}

}
