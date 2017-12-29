package com.pushpopsoft.jsonrpclib.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.pushpopsoft.jsonrpclib.server.exceptions.BaseErrorException;

public interface MethodDispatcher {
    JsonNode dispatch(JsonNode params) throws BaseErrorException;
}
