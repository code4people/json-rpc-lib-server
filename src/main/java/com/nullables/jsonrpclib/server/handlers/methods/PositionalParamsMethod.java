package com.nullables.jsonrpclib.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.nullables.jsonrpclib.server.exceptions.BaseErrorException;
import com.nullables.jsonrpclib.server.exceptions.InvalidParamsException;

import java.util.stream.StreamSupport;

public class PositionalParamsMethod {
    private final int mandatoryParamsCount;
    private final JsonMethodAdapter jsonMethodAdapter;
    private final int paramsCount;

    public PositionalParamsMethod(
            int mandatoryParamsCount,
            int optionalParamsCount,
            JsonMethodAdapter jsonMethodAdapter) {
        if (optionalParamsCount < 0) {
            throw new IllegalArgumentException("'paramsCount' must be >= 0");
        }
        if (mandatoryParamsCount < 0) {
            throw new IllegalArgumentException("'mandatoryParamsCount' must be >= 0");
        }
        this.jsonMethodAdapter = jsonMethodAdapter;
        this.mandatoryParamsCount = mandatoryParamsCount;
        this.paramsCount = mandatoryParamsCount + optionalParamsCount;
    }

    public JsonNode invoke(ArrayNode jsonParams) throws BaseErrorException {
        if (jsonParams.size() < mandatoryParamsCount) {
            String message = String.format("Number of parameters received: '%s', required: '%s'", jsonParams.size(), mandatoryParamsCount);
            throw new InvalidParamsException(message);
        }
        JsonNode[] normalizeParams = normalizeParams(jsonParams);
        return jsonMethodAdapter.invoke(normalizeParams);
    }

    private JsonNode[] normalizeParams(ArrayNode jsonParams) {
        if (paramsCount == jsonParams.size()) {
            return StreamSupport.stream(jsonParams.spliterator(), false)
                    .toArray(JsonNode[]::new);
        }
        JsonNode[] result = new JsonNode[paramsCount];
        int itemsToCopy = jsonParams.size() > paramsCount ? paramsCount : jsonParams.size();
        for (int i = 0; i < itemsToCopy; i++) {
            result[i] = jsonParams.get(i);
        }
        for (int i = jsonParams.size(); i < result.length; i++) {
            result[i] = MissingNode.getInstance();
        }
        return result;
    }

    public int getMandatoryParamsCount() {
        return mandatoryParamsCount;
    }

    public int getParamsCount() {
        return paramsCount;
    }
}
