package com.code4people.jsonrpclib.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;

public interface MethodDispatcher {
    JsonNode dispatch(JsonNode params) throws BaseErrorException;
}
