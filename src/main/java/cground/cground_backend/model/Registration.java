package cground.cground_backend.model;

public class Registration {
    private String username;
    private String password;
    private String fullName;
    private String userType; // "TENANT" or "LANDLORD"

    public Registration(){
        super();
    }
    
    public Registration(String username, String password, String fullName, String userType) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String toString(){
        return (
                "Registration info: Email : " + this.username +
                " password : "+ this.password + " Full Name : " + this.fullName +
                " User Type: " + this.userType);
    }
}
