package com.code4people.jsonrpclib.server.handlers;

import com.code4people.jsonrpclib.server.serialization.SerializationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.code4people.jsonrpclib.server.model.Output;
import com.code4people.jsonrpclib.server.model.Request;
import com.code4people.jsonrpclib.server.model.Response;
import com.code4people.jsonrpclib.server.serialization.SerializationService;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class JsonRequestHandler {

    private final SerializationService serializationService;
    private final DebugErrorFactory debugErrorFactory;
    private final RequestHandler requestHandler;

    public JsonRequestHandler(SerializationService serializationService, DebugErrorFactory debugErrorFactory, RequestHandler requestHandler) {
        this.serializationService = serializationService;
        this.debugErrorFactory = debugErrorFactory;
        this.requestHandler = requestHandler;
    }

    public Optional<Output> processJsonNodeMessage(JsonNode message) {
        if (message.isArray()) {
            ArrayNode jsonRequests = (ArrayNode) message;
            if (jsonRequests.size() == 0) {
                JsonNode debugErrorData = debugErrorFactory.create("Request batch cannot be empty", null);
                Response response = Response.createInvalidRequestError(debugErrorData);
                return Optional.of(new Output(response));
            }
            return processMultipleRequests(jsonRequests).map(Output::new);
        }
        else {
            return processSingleRequest(message).map(Output::new);
        }
    }

    private Optional<Response> processSingleRequest(JsonNode jsonRequest) {
        Request request;
        try {
            request = serializationService.deserialize(jsonRequest);
        }
        catch (SerializationException e) {
            JsonNode debugErrorData = debugErrorFactory.create("Deserialization of request json failed", e);
            return Optional.of(Response.createInvalidRequestError(debugErrorData));
        }
        return requestHandler.processRequest(request);
    }

    private Optional<Response[]> processMultipleRequests(ArrayNode jsonRequests) {
        Response[] responses = StreamSupport.stream(jsonRequests.spliterator(), false)
            .map(this::processSingleRequest)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toArray(Response[]::new);
        return responses.length == 0 ? Optional.empty() : Optional.of(responses);
    }
}
