package com.emperium.errors;

/**
 * Class used to produce error messages in the response
 */
public class ErrorMessage {
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    public String getMessage() {
        return error;
    }
}
