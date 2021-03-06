package com.code4people.jsonrpclib.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;

public class SingleParamMethod {
    private final JsonMethodAdapter jsonMethodAdapter;

    public SingleParamMethod(JsonMethodAdapter jsonMethodAdapter) {
        this.jsonMethodAdapter = jsonMethodAdapter;
    }

    public JsonNode invoke(JsonNode param) throws BaseErrorException {
        return jsonMethodAdapter.invoke(new JsonNode[] { param });
    }
}
