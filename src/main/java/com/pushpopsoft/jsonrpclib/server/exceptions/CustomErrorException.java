package com.pushpopsoft.jsonrpclib.server.exceptions;

public class CustomErrorException extends RuntimeException {
    private final int code;
    private final Object data;

    public CustomErrorException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public CustomErrorException(int code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
