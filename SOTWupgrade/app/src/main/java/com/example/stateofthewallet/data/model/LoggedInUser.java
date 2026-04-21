package com.example.stateofthewallet.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String usrpassword;

    public LoggedInUser(String userId, String displayName, String usrpassword ) {
        this.userId = userId;
        this.displayName = displayName;
        this.usrpassword = usrpassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsrpassword() {
        return usrpassword;
    }

    public void setUsrpassword(String usrpassword) {
        this.usrpassword = usrpassword;
    }
}