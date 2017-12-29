package com.nullables.jsonrpclib.server.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.nullables.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.nullables.jsonrpclib.server.model.Response;
import com.nullables.jsonrpclib.server.serialization.SerializationException;
import com.nullables.jsonrpclib.server.serialization.SerializationService;
import com.nullables.jsonrpclib.server.ServiceActivator;

import java.util.Optional;

public class ServiceActivatorImpl implements ServiceActivator {

    private final SerializationService serializationService;
    private final JsonRequestHandler jsonRequestHandler;
    private final DebugErrorFactory debugErrorFactory;

    public ServiceActivatorImpl(SerializationService serializationService, JsonRequestHandler jsonRequestHandler, DebugErrorFactory debugErrorFactory) {
        this.serializationService = serializationService;
        this.jsonRequestHandler = jsonRequestHandler;
        this.debugErrorFactory = debugErrorFactory;
    }

    public Optional<String> processMessage(String message) {
        JsonNode jsonNode;
        try {
            jsonNode = serializationService.deserializeToJsonNode(message);
        } catch (SerializationException se) {
            JsonNode errorData = debugErrorFactory.create(null, se);
            Response response = Response.createParseError(errorData);
            return Optional.of(serializeResponseWithError(response));
        }
        return jsonRequestHandler.processJsonNodeMessage(jsonNode)
                .map(outputObject -> {
                    if (outputObject.isBatch()) {
                        return serializeBatch(outputObject.getBatch());
                    } else {
                        return serializeResponse(outputObject.getSingle());
                    }
                });
    }

    private String serializeBatch(Response[] batch) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < batch.length - 1; i++) {
            Response response = batch[i];
            String responseString = serializeResponse(response);
            sb.append(responseString);
            sb.append(',');
        }
        Response response = batch[batch.length - 1];
        String responseString = serializeResponse(response);
        sb.append(responseString);
        sb.append(']');
        return sb.toString();
    }

    private String serializeResponse(Response response) {
        try {
            return serializationService.serializeToString(response);
        } catch (SerializationException e) {
            JsonNode errorData = debugErrorFactory.create("Failed to deserialize response.", e);
            Response responseWithError = Response.createInternalError(response.getId(), errorData);
            return serializeResponseWithError(responseWithError);
        }
    }

    private String serializeResponseWithError(Response response) {
        try {
            return serializationService.serializeToString(response);
        } catch (SerializationException e) {
            throw new Error("Unable to serialize response error.", e);
        }
    }
}
