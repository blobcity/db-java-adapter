package com.blobcity.db.config;

/**
 * Holds the default credentials for the application. If you require to set run time credentials that are different from the default for the application, this
 * class is not for you. Please look for appropriate {@link CloudStorage} methods
 *
 * @author Karun AB <karun.ab@blobcity.net>
 */
public class Credentials {

    private final String username;
    private final String password;
    private final String db;
    private static Credentials instance;

    private Credentials(final String username, final String password, final String db) {
        this.username = username;
        this.password = password;
        this.db = db;
    }

    public static Credentials getInstance() {
        return instance;
    }

    public static Credentials init(final String username, final String password, final String db) {
        if (instance != null) {
            throw new IllegalStateException("Attempting to change application credentails. "
                    + "Application credentails cannot be changed once set.");
        }

        instance = new Credentials(username, password, db);
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDb() {
        return db;
    }
}
