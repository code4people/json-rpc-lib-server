package com.nullables.jsonrpclib.server.exceptions;

public class BaseErrorException extends Exception {
    public BaseErrorException(String message) {
        super(message);
    }

    public BaseErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
