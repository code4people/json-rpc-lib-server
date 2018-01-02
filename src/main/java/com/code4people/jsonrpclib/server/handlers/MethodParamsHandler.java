package com.code4people.jsonrpclib.server.handlers;

import com.code4people.jsonrpclib.server.handlers.dispatch.MethodDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;

import java.util.Map;

public class MethodParamsHandler {

    private final Map<String, MethodDispatcher> methodMap;

    public MethodParamsHandler(Map<String, MethodDispatcher> methodMap) {
        this.methodMap = methodMap;
    }

    public JsonNode processMethodParams(String methodName, JsonNode params) throws BaseErrorException {
        if (!methodMap.containsKey(methodName)) {
            String message = String.format("Method '%s' doesn't exist.", methodName);
            throw new IllegalArgumentException(message);
        }
        MethodDispatcher methodDispatcher = methodMap.get(methodName);
        return methodDispatcher.dispatch(params);
    }

    public boolean isExistingMethod(String methodName) {
        return methodMap.containsKey(methodName);
    }
}
