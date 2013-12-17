
package com.blobcity.adminpanel.exceptions;

import java.util.List;

/**
 * Used to indicate failure due to bad data when performing DB operations
 * 
 * @author akshay
 */
public class ValidationException extends Exception {

    /**
     * Allows multiple validation messages to be set.
     * Messages must be in the localized form depending on the AppId of the client
     */
    private List<String> messages;

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
