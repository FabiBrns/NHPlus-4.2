package de.hitec.nhplus.model;

public class User {
    private long UID;
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(long UID, String username, String password) {
        this.UID = UID;
        this.username = username;
        this.password = password;
    }

    public long getUID() {
        return UID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
