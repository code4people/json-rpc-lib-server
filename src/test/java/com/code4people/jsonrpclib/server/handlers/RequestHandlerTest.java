package com.code4people.jsonrpclib.server.handlers;

import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.exceptions.SpecificServerErrorException;
import com.code4people.jsonrpclib.server.model.Request;
import com.code4people.jsonrpclib.server.model.Response;
import com.code4people.jsonrpclib.server.model.ResponseError;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestHandlerTest {
    @Test
    public void processRequest_shouldReturnResponse() throws Exception {
        JsonNode errorData = TextNode.valueOf("result");
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", 1, params);
        when(methodParamsHandler.isExistingMethod("method")).thenReturn(true);
        when(methodParamsHandler.processMethodParams("method", params)).thenReturn(errorData);
        Response response = requestHandler.processRequest(request).get();

        assertEquals(1, response.getId());
        assertEquals(errorData, response.getResult());
        assertNull(response.getError());
    }

    @Test
    public void processRequest_shouldReturnErrorResponse_whenMethodWasNotFound() throws Exception {
        JsonNode errorData = TextNode.valueOf("result");
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", 1, params);
        when(methodParamsHandler.isExistingMethod("method")).thenReturn(false);
        when(debugErrorFactory.create("Method 'method' doesn't exist.", null)).thenReturn(errorData);

        Response response = requestHandler.processRequest(request).get();

        assertEquals(1, response.getId());
        assertNull(response.getResult());
        Assert.assertEquals(ResponseError.METHOD_NOT_FOUND_CODE, response.getError().getCode());
        assertNotNull(response.getError().getMessage());
        assertEquals(errorData, response.getError().getData());
    }

    @Test
    public void processRequest_shouldReturnErrorResponse_whenParamsAreInvalid() throws Exception {
        JsonNode errorData = TextNode.valueOf("result");
        Throwable cause = new Throwable();
        InvalidParamsException exception = new InvalidParamsException("detailMessage", cause);
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", 1, params);
        when(methodParamsHandler.isExistingMethod("method")).thenReturn(true);
        when(methodParamsHandler.processMethodParams("method", params)).thenThrow(exception);
        when(debugErrorFactory.create("detailMessage", cause)).thenReturn(errorData);

        Response response = requestHandler.processRequest(request).get();

        assertEquals(1, response.getId());
        assertNull(response.getResult());
        assertEquals(ResponseError.INVALID_PARAMS_CODE, response.getError().getCode());
        assertNotNull(response.getError().getMessage());
        assertEquals(errorData, response.getError().getData());
    }

    @Test
    public void processRequest_shouldReturnErrorResponse_whenBaseErrorExceptionWasThrown() throws Exception {
        TextNode errorData = TextNode.valueOf("error Data");
        SpecificServerErrorException exception = new SpecificServerErrorException(9, "detailMessage", errorData, new Throwable());
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", 1, params);
        when(methodParamsHandler.isExistingMethod("method")).thenReturn(true);
        when(methodParamsHandler.processMethodParams("method", params)).thenThrow(exception);

        Response response = requestHandler.processRequest(request).get();

        assertEquals(1, response.getId());
        assertNull(response.getResult());
        assertEquals(9, response.getError().getCode());
        assertEquals("detailMessage", response.getError().getMessage());
        assertEquals(errorData, response.getError().getData());
    }

    @Test
    public void processRequest_shouldReturnNull_whenRequestIdIsNullAndAnyBaseErrorExceptionWasThrown() throws Exception {
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", null, params);
        when(methodParamsHandler.processMethodParams("method", params)).thenThrow(new BaseErrorException("detailMessage", null));

        Optional<Response> response = requestHandler.processRequest(request);

        assertFalse(response.isPresent());
    }

    @Test
    public void processRequest_shouldReturnNull_whenRequestIdIsNullAndDispatchIsSuccessful() throws Exception {
        MethodParamsHandler methodParamsHandler = mock(MethodParamsHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        JsonNode params = new ObjectMapper().createObjectNode();
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        Request request = new Request("2.0", "method", null, params);
        when(methodParamsHandler.processMethodParams("method", params)).thenReturn(new TextNode(""));

        Optional<Response> response = requestHandler.processRequest(request);

        assertFalse(response.isPresent());
    }
}