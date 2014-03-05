
package com.blobcity.db.constants;

/**
 *
 * @author Sanket Sarang <sanket@blobcity.net>
 */
public class Credentials {
    private String appId;
    private String appKey;
    private boolean initialized;
    
    private Credentials() {
    }
    
    public static Credentials getInstance() {
        return CredentialsHolder.INSTANCE;
    }
    
    private static class CredentialsHolder {
        private static final Credentials INSTANCE = new Credentials();
    }
    
    public void init(final String appId, final String appKey) {
        if(initialized) {
            throw new IllegalStateException("Attempting to change application credentails. "
                    + "Application credentails cannot be changed once set.");
        }
        this.appId = appId;
        this.appKey = appKey;
        initialized = true;
    }

    public String getAppId() {
        return appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
