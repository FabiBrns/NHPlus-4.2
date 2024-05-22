package de.hitec.nhplus.model;

public class User {
    private long uid;
    private String username;
    private String password;

    /**
     * Constructor to initiate an object of class <code>User</code> with the given parameter. Use this constructor
     * to initiate objects, which are not persisted yet, because it will not have a user id (uid).
     *
     * @param username username of the user.
     * @param password password of the user.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor to initiate an object of class <code>User</code> with the given parameter. Use this constructor
     * to initiate objects, which are already persisted and have a user id (uid).
     *
     * @param uid      Patient id.
     * @param username First name of the patient.
     * @param password Last name of the patient.
     */
    public User(long uid, String username, String password) {
        this.uid = uid;
        this.username = username;
        this.password = password;
    }

    public long getUid() {
        return uid;
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
