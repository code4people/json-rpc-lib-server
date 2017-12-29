package com.nullables.jsonrpclib.server.exceptions;

public class InvalidParamsException extends BaseErrorException {
    public InvalidParamsException(String message) {
        super(message);
    }

    public InvalidParamsException(String message, Throwable cause) {
        super(message, cause);
    }
}
