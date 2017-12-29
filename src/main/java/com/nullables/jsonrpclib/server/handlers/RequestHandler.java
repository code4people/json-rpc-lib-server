package com.nullables.jsonrpclib.server.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.nullables.jsonrpclib.server.exceptions.BaseErrorException;
import com.nullables.jsonrpclib.server.exceptions.InvalidParamsException;
import com.nullables.jsonrpclib.server.exceptions.SpecificServerErrorException;
import com.nullables.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.nullables.jsonrpclib.server.model.Request;
import com.nullables.jsonrpclib.server.model.Response;

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
                JsonNode errorData = debugErrorFactory.create(ex.getMessage(), ex.getCause());
                response = Response.createInvalidParamsError(id, errorData);
            }
            catch (SpecificServerErrorException ex) {
                response = Response.createError(id, ex.getCode(), ex.getMessage(), ex.getData());
            }
            catch (BaseErrorException ex) {
                JsonNode errorData = debugErrorFactory.create(ex.getMessage(), ex.getCause());
                response = Response.createInternalError(id, errorData);
            }
        }
        else {
            String message = String.format("Method '%s' doesn't exist.", methodName);
            JsonNode errorData = debugErrorFactory.create(message, null);
            response = Response.createMethodNotFoundError(id, errorData);
        }

        // TODO: implement logging of response

        if (id == null) {
            return Optional.empty();
        }

        return Optional.of(response);
    }
}
