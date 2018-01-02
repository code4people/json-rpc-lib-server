package com.code4people.jsonrpclib.server.server.handlers;

import com.code4people.jsonrpclib.server.handlers.MethodParamsHandler;
import com.code4people.jsonrpclib.server.handlers.dispatch.MethodDispatcher;
import com.code4people.jsonrpclib.server.handlers.dispatch.OverloadedMethodDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MethodParamsHandlerTest {
    @Test
    public void isExistingMethod_shouldReturnFalse_whenThereIsNoSuchMethod() throws Exception {
        MethodParamsHandler methodParamsHandler = new MethodParamsHandler(Collections.emptyMap());

        boolean result = methodParamsHandler.isExistingMethod("method");

        assertFalse(result);
    }

    @Test
    public void isExistingMethod_shouldReturnTrue_whenThereIsMethod() throws Exception {
        MethodParamsHandler methodParamsHandler = new MethodParamsHandler(Collections.singletonMap("method", null));

        boolean result = methodParamsHandler.isExistingMethod("method");

        assertTrue(result);
    }

    @Test
    public void dispatch_shouldReturnResult() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        MethodDispatcher methodDispatcher = Mockito.mock(OverloadedMethodDispatcher.class, Mockito.RETURNS_SMART_NULLS);
        Map<String, MethodDispatcher> methodMap = new HashMap<>();
        methodMap.put("method", methodDispatcher);
        ObjectNode params = new ObjectMapper().createObjectNode();
        when(methodDispatcher.dispatch(params)).thenReturn(EXPECTED_RESULT);
        MethodParamsHandler methodParamsHandler = new MethodParamsHandler(methodMap);

        JsonNode result = methodParamsHandler.processMethodParams("method", params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void dispatch_shouldThrow_whenThereIsNoSuchMethod() throws Exception {
        Map<String, MethodDispatcher> methodMap = Collections.emptyMap();
        JsonNode params = new ObjectMapper().createObjectNode();
        MethodParamsHandler methodParamsHandler = new MethodParamsHandler(methodMap);

        methodParamsHandler.processMethodParams("method", params);
    }
}