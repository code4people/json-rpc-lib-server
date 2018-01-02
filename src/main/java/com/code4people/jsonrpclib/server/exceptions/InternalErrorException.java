package com.code4people.jsonrpclib.server.exceptions;

public class InternalErrorException extends BaseErrorException {

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
