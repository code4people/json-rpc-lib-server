package com.code4people.jsonrpclib.server.handlers.methods;

import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.util.Arrays;

public class MissingParamsMethod {
    private final JsonMethodAdapter jsonMethodAdapter;
    private final int numberOfOptionalParams;

    public MissingParamsMethod(JsonMethodAdapter jsonMethodAdapter, int numberOfOptionalParams) {
        this.jsonMethodAdapter = jsonMethodAdapter;
        this.numberOfOptionalParams = numberOfOptionalParams;
    }

    public JsonNode invoke() throws BaseErrorException {
        JsonNode[] params = new JsonNode[numberOfOptionalParams];
        Arrays.fill(params, MissingNode.getInstance());
        return jsonMethodAdapter.invoke(params);
    }
}
