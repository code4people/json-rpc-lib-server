package com.pushpopsoft.jsonrpclib.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.pushpopsoft.jsonrpclib.server.exceptions.BaseErrorException;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.JsonMethodAdapter;

public class SingleArgumentMethodDispatcher implements MethodDispatcher {
    private final JsonMethodAdapter jsonMethodAdapter;

    public SingleArgumentMethodDispatcher(JsonMethodAdapter jsonMethodAdapter) {
        this.jsonMethodAdapter = jsonMethodAdapter;
    }

    public JsonNode dispatch(JsonNode param) throws BaseErrorException {
        return jsonMethodAdapter.invoke(new JsonNode[] { param });
    }
}
