package com.blobcity.db.config;

import com.blobcity.db.Db;
import com.blobcity.db.exceptions.InternalAdapterException;
import com.blobcity.db.search.StringUtil;

/**
 * Holds the default credentials for the application. If you require to set run time credentials that are different from the default for the application, this
 * class is not for you. Please look for appropriate {@link Db} methods
 *
 * @author Karun AB
 */
public class Credentials {

    private final String serverAddress;
    private final String username;
    private final String password;
    private String db;
    private static Credentials instance;
    private static final String DEFAULT_SERVER_ADDRESS = "ds.blobcity.com";

    private Credentials(final String serverAddress, final String username, final String password, final String db) {
        this.serverAddress = serverAddress;
        this.username = username;
        this.password = password;
        this.db = db;
    }

    public static Credentials getInstance() {
        if (instance == null) {
            throw new InternalAdapterException("Credentials have not been setup before a request was made.");
        }
        return instance;
    }

    //Called only for stored procs
    public static Credentials getInstanceNullOrNotNull() {
        if (instance == null) {
            System.out.println("Credentials have not been setup before a request was made.");
        }

        return instance;
    }

    public static Credentials init(final String username, final String password, final String db) {
        return init(DEFAULT_SERVER_ADDRESS, username, password, db);
    }

    public static Credentials init(final String serverAddress, final String username, final String password, final String db) {
        if (instance != null) {
            throw new IllegalStateException("Attempting to change application credentails. "
                    + "Application credentails cannot be changed once set.");
        }

        return instance = create(serverAddress, username, password, db);
    }

    public static Credentials create(final String username, final String password, final String db) {
        return create(DEFAULT_SERVER_ADDRESS, username, password, db);
    }

    public static Credentials create(final String serverAddress, final String username, final String password, final String db) {
        return new Credentials(serverAddress, username, password, db);
    }

    public static Credentials create(final Credentials credentials, final String serverAddress, final String username, final String password, final String db) {
        return new Credentials(
                StringUtil.isEmpty(serverAddress) ? credentials.getServiceAddress() : serverAddress,
                StringUtil.isEmpty(username) ? credentials.getUsername() : username,
                StringUtil.isEmpty(password) ? credentials.getPassword() : password,
                StringUtil.isEmpty(db) ? credentials.getDb() : db);
    }

    public String getServiceAddress() {
        return serverAddress;
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

    public void setDb(String db) {
        this.db = db;
    }
}
