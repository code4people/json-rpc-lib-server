package com.code4people.jsonrpclib.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.code4people.jsonrpclib.server.handlers.methods.MissingParamsMethod;
import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.NamedParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.PositionalParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.methods.NamedParamsMethod;
import com.code4people.jsonrpclib.server.handlers.methods.PositionalParamsMethod;

import java.util.HashMap;
import java.util.Map;

public class GranularParamsMethodDispatcher implements MethodDispatcher {
    private final PositionalParamsMethodResolver positionalParamsMethodResolver;
    private final NamedParamsMethodResolver namedParamsMethodResolver;
    private final MissingParamsMethod missingParamsMethod;

    public GranularParamsMethodDispatcher(PositionalParamsMethodResolver positionalParamsMethodResolver, NamedParamsMethodResolver namedParamsMethodResolver, MissingParamsMethod missingParamsMethod) {
        this.positionalParamsMethodResolver = positionalParamsMethodResolver;
        this.namedParamsMethodResolver = namedParamsMethodResolver;
        this.missingParamsMethod = missingParamsMethod;
    }

    @Override
    public JsonNode dispatch(JsonNode params) throws BaseErrorException {
        if (params instanceof ObjectNode) {
            ObjectNode namedParams = (ObjectNode)params;
            return invokeWithNamedParams(namedParams);
        }
        else if (params instanceof ArrayNode) {
            ArrayNode positionalParams = (ArrayNode)params;
            return invokeWithPositionalParams(positionalParams);
        }
        else if (params instanceof MissingNode
                || params instanceof NullNode
                || params == null) {
            return invokeWithoutParams();
        }
        else {
            throw new InvalidParamsException("Illegal params format.");
        }
    }

    private JsonNode invokeWithPositionalParams(ArrayNode params) throws BaseErrorException {
        PositionalParamsMethod method = positionalParamsMethodResolver.resolve(params.size());
        return method.invoke(params);
    }

    private JsonNode invokeWithNamedParams(ObjectNode params) throws BaseErrorException {
        Map<String, JsonNode> paramsMap = new HashMap<>(params.size());
        params.fields().forEachRemaining(e -> paramsMap.put(e.getKey(), e.getValue()));
        NamedParamsMethod method = namedParamsMethodResolver.resolve(paramsMap.keySet());
        return method.invoke(paramsMap);
    }

    private JsonNode invokeWithoutParams() throws BaseErrorException {
        if (missingParamsMethod != null) {
            return missingParamsMethod.invoke();
        }
        else {
            throw new InvalidParamsException("Missing parameters are not allowed for this method.");
        }
    }
}
