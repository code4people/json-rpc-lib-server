package com.code4people.jsonrpclib.server.handlers.errorresolving;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ServerError {
    public final int code;
    public final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Object data;

    public static ServerError of(int code, String message, Object data) {
        return new ServerError(code, message, data);
    }

    private ServerError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}