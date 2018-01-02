package com.code4people.jsonrpclib.server.handlers.methods;

import com.code4people.jsonrpclib.server.exceptions.*;
import com.code4people.jsonrpclib.server.handlers.errorresolving.ServerError;
import com.code4people.jsonrpclib.server.serialization.ResultSerializer;
import com.code4people.jsonrpclib.server.serialization.SerializationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.code4people.jsonrpclib.server.exceptions.*;
import com.code4people.jsonrpclib.server.handlers.errorresolving.MethodErrorMapping;
import com.code4people.jsonrpclib.server.serialization.ParamsDeserializer;
import com.code4people.jsonrpclib.server.exceptions.*;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

public class JsonMethodAdapter {
    private final Method method;
    private final Type[] types;
    private final boolean isVoid;
    private final MethodErrorMapping methodErrorMapping;
    private final ParamsDeserializer paramsDeserializer;
    private final ResultSerializer resultSerializer;

    public JsonMethodAdapter(Method method,
                             Type[] types,
                             boolean isVoid,
                             MethodErrorMapping methodErrorMapping,
                             ParamsDeserializer paramsDeserializer,
                             ResultSerializer resultSerializer) {
        this.method = method;
        this.types = types;
        this.isVoid = isVoid;
        this.methodErrorMapping = methodErrorMapping;
        this.paramsDeserializer = paramsDeserializer;
        this.resultSerializer = resultSerializer;
    }

    public JsonNode invoke(JsonNode[] jsonParams) throws BaseErrorException {
        Objects.requireNonNull(jsonParams, "'jsonParams' cannot be null");
        if (jsonParams.length != types.length) {
            String message = String.format("Number of json params has to be '%s' but is '%s'.", types.length, jsonParams.length);
            throw new IllegalArgumentException(message);
        }

        Object[] params = new Object[types.length];
        for (int i = 0; i < jsonParams.length; i++) {
            try {
                params[i] = paramsDeserializer.deserialize(jsonParams[i], types[i]);
            }
            catch (SerializationException ex) {
                String message = String.format("Failed to deserialize parameter value: '%s'.", jsonParams[i]);
                throw new InvalidParamsException(message, ex);
            }
        }

        Object result;
        try {
            result = method.invoke(params);
        }
        catch (BaseErrorException ex) {
            throw ex;
        }
        catch (CustomErrorException ex) {
            JsonNode data;
            try {
                data = resultSerializer.serialize(ex.getData());
            }
            catch (SerializationException sex) {
                throw new InternalErrorException("Failed to serialize error data from CustomErrorException.", sex);
            }

            throw new SpecificServerErrorException(ex.getCode(), ex.getMessage(), data, ex);
        }
        catch (Throwable t) {
            Optional<ServerError> serverErrorOptional = methodErrorMapping.resolve(t);
            if (serverErrorOptional.isPresent()) {
                ServerError serverError = serverErrorOptional.get();
                throw new SpecificServerErrorException(serverError.code, serverError.message, null, t);
            }

            throw new InternalErrorException("Exception was thrown inside application.", t);
        }

        if (isVoid) {
            return MissingNode.getInstance();
        }

        JsonNode jsonResult;
        try {
            jsonResult = resultSerializer.serialize(result);
        }
        catch (SerializationException ex) {
            throw new InternalErrorException("Failed to serialize the result of invocation.", ex);
        }
        return jsonResult == null ? NullNode.getInstance() : jsonResult;
    }

}
