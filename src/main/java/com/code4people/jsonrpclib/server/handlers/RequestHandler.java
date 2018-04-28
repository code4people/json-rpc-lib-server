package com.code4people.jsonrpclib.server.handlers;

import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.exceptions.SpecificServerErrorException;
import com.code4people.jsonrpclib.server.model.Request;
import com.code4people.jsonrpclib.server.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;

import java.util.Optional;

public class RequestHandler {
    private final MethodParamsHandler methodParamsHandler;
    private final DebugErrorFactory debugErrorFactory;

    public RequestHandler(MethodParamsHandler methodParamsHandler, DebugErrorFactory debugErrorFactory) {

        this.methodParamsHandler = methodParamsHandler;
        this.debugErrorFactory = debugErrorFactory;
    }

    public Optional<Response> processRequest(Request request) {
        Object id = request.getId();
        String methodName = request.getMethod();
        Response response;
        if (methodParamsHandler.isExistingMethod(methodName)) {
            try {
                JsonNode result = methodParamsHandler.processMethodParams(methodName, request.getParams());
                response = Response.create(id, result);
            }
            catch (InvalidParamsException ex) {
                JsonNode debugErrorData = debugErrorFactory.create(ex.getMessage(), ex.getCause());
                response = Response.createInvalidParamsError(id, debugErrorData);
            }
            catch (SpecificServerErrorException ex) {
                JsonNode debugErrorData = debugErrorFactory.create(ex.getMessage(), ex.getCause());
                response = Response.createError(id, ex.getCode(), ex.getMessage(), ex.getData(), debugErrorData);
            }
            catch (BaseErrorException ex) {
                JsonNode debugErrorData = debugErrorFactory.create(ex.getMessage(), ex.getCause());
                response = Response.createInternalError(id, debugErrorData);
            }
        }
        else {
            String message = String.format("Method '%s' doesn't exist.", methodName);
            JsonNode debugErrorData = debugErrorFactory.create(message, null);
            response = Response.createMethodNotFoundError(id, debugErrorData);
        }

        // TODO: implement logging of response

        if (id == null) {
            return Optional.empty();
        }

        return Optional.of(response);
    }
}
