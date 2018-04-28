package com.code4people.jsonrpclib.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class ResponseError {

    public static final int PARSE_ERROR_CODE = -32700;
    public static final int INVALID_REQUEST_CODE = -32600;
    public static final int METHOD_NOT_FOUND_CODE = -32601;
    public static final int INVALID_PARAMS_CODE = -32602;
    public static final int INTERNAL_ERROR_CODE = -32603;

    private final int code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final JsonNode data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("_debugErrorData")
    private final JsonNode debugErrorData;

    public ResponseError(int code,
                         String message,
                         JsonNode data,
                         JsonNode debugErrorData) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.debugErrorData = debugErrorData;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonNode getData() {
        return data;
    }

    public JsonNode getDebugErrorData() {
        return debugErrorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseError error = (ResponseError) o;

        if (code != error.code) return false;
        if (message != null ? !message.equals(error.message) : error.message != null) return false;
        return data != null ? data.equals(error.data) : error.data == null;
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
