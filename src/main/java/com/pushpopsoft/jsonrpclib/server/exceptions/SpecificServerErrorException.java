package com.pushpopsoft.jsonrpclib.server.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

public class SpecificServerErrorException extends BaseErrorException {
    private final int code;
    private final JsonNode data;

    public SpecificServerErrorException(int code, String message, JsonNode data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public JsonNode getData() {
        return data;
    }
}
