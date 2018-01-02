package com.code4people.jsonrpclib.server.handlers.dispatch;

import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.code4people.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import com.fasterxml.jackson.databind.JsonNode;

public class SingleArgumentMethodDispatcher implements MethodDispatcher {
    private final JsonMethodAdapter jsonMethodAdapter;

    public SingleArgumentMethodDispatcher(JsonMethodAdapter jsonMethodAdapter) {
        this.jsonMethodAdapter = jsonMethodAdapter;
    }

    public JsonNode dispatch(JsonNode param) throws BaseErrorException {
        return jsonMethodAdapter.invoke(new JsonNode[] { param });
    }
}
