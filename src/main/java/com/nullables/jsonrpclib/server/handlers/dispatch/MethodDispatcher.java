package com.nullables.jsonrpclib.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.nullables.jsonrpclib.server.exceptions.BaseErrorException;

public interface MethodDispatcher {
    JsonNode dispatch(JsonNode params) throws BaseErrorException;
}
