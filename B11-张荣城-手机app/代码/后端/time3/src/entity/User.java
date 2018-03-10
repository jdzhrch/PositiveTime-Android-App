package entity;

public class User {
    public String email;
    public String username;
    public String password;
    public int status ;
    public int classification;
    public User() {
    }
    public User(String email,String username, String password,int status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
        this.classification = -1;
    }

    public User(String email,String username, String password,int status,int c) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.status = status;
        this.classification = c;
    }

    public int getClassification(){return classification;}

    public void setClassification(int c){this.classification = c;}

    public String getPassword(){return password;};

    public void setPassword(){this.password = password;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail(){return email;};

    public void setEmail(String e){this.email = e;}

    public int getStatus(){return this.status;};

    public void setStatus(int status){this.status = status;}

}
