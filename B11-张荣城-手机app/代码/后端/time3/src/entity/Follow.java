package entity;

public class Follow {

    public String email;
    public String following;


    public Follow() {
    }

    public Follow(String e,String f) {
        this.email = e;
        this.following = f;
    }
    public String getEmail(){return this.email;}

    public void setEmail(String e){
        this.email = e;
    }

    public String getFollowing(){
        return this.following;
    }

    public void setFollowing(String f){
        this.following = f;
    }

}
