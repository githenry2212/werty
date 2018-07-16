package com.yang.werty.loader.exception;

/**
 * exception
 */
public class LoaderException extends Exception {

    public LoaderException(String message) {
        super(message);
    }

    public LoaderException(Throwable t) {
        super(t);
    }

    public LoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
