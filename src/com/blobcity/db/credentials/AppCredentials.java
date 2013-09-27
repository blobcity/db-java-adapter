/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blobcity.db.credentials;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class AppCredentials {
    private String account ="";
    private String user = "";
    private String token = "";
    
    private boolean set = false;

    private AppCredentials() {
    }
    
    public static AppCredentials getInstance() {
        return AppCredentialsHolder.INSTANCE;
    }
    
    private static class AppCredentialsHolder {

        private static final AppCredentials INSTANCE = new AppCredentials();
    }
    
    public String getAccount() {
        return account;
    }

    public String getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
    
    public void setCredentials(final String account, final String user, final String token) {
        if(set) {
            throw new IllegalStateException("Attempting overwrite already set credentials.");
        }
        this.account = account;
        this.user = user;
        this.token = token;
        set = true;
    }
}
