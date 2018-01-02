package com.code4people.jsonrpclib.server.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.code4people.jsonrpclib.server.handlers.methods.MissingParamsMethod;
import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.handlers.dispatch.OverloadedMethodDispatcher;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.NamedParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.PositionalParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.methods.NamedParamsMethod;
import com.code4people.jsonrpclib.server.handlers.methods.PositionalParamsMethod;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OverloadedMethodDispatcherTest {
    @Test
    public void invokeWithNamedParams_shouldReturnResult_whenPassingNamedParams() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        Map<String, JsonNode> paramsMap = new HashMap<>();
        paramsMap.put("a", new TextNode("a"));
        ObjectNode params = new ObjectMapper()
                .createObjectNode()
                .put("a", "a");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        NamedParamsMethod method = mock(NamedParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        MissingParamsMethod missingParamsMethod = mock(MissingParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
        when(method.invoke(paramsMap)).thenReturn(EXPECTED_RESULT);
        when(namedParamsMethodResolver.resolve(paramsMap.keySet())).thenReturn(method);

        Object result = overloadedMethodDispatcher.dispatch(params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void invokeWithNamedParams_shouldThrow_whenNamedParamsMatcherThrowsInvalidParamsException() throws Exception {
        Map<String, JsonNode> jsonParams = new HashMap<>();
        jsonParams.put("a", new TextNode("a"));
        ObjectNode params = new ObjectMapper()
                .createObjectNode()
                .put("a", "a");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        MissingParamsMethod missingParamsMethod = mock(MissingParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
        when(namedParamsMethodResolver.resolve(jsonParams.keySet())).thenThrow(new InvalidParamsException(""));

        overloadedMethodDispatcher.dispatch(params);
    }

    @Test
    public void invokeWithPositionalParams_shouldReturnResult_whenPassingPositionalParams() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add("a");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class);
        PositionalParamsMethod method = mock(PositionalParamsMethod.class);
        MissingParamsMethod missingParamsMethod = mock(MissingParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
        when(method.invoke(params)).thenReturn(EXPECTED_RESULT);
        when(positionalParamsMethodResolver.resolve(1)).thenReturn(method);

        Object result = overloadedMethodDispatcher.dispatch(params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void invokeWithPositionalParams_shouldThrow_whenPositionalParamsMatcherThrowsInvalidParamsException() throws Exception {
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add("a");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class, Mockito.RETURNS_SMART_NULLS);
        MissingParamsMethod missingParamsMethod = mock(MissingParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
        when(positionalParamsMethodResolver.resolve(1)).thenThrow(new InvalidParamsException(""));

        overloadedMethodDispatcher.dispatch(params);
    }

    @Test
    public void invokeWithoutParams_shouldReturnResult_whenPassingPositionalParams() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class);
        MissingParamsMethod missingParamsMethod = mock(MissingParamsMethod.class, Mockito.RETURNS_SMART_NULLS);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
        when(missingParamsMethod.invoke()).thenReturn(EXPECTED_RESULT);

        Object result = overloadedMethodDispatcher.dispatch(MissingNode.getInstance());

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void invokeWithoutParams_shouldThrow_whenMissingParamsMethodIsNull() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        PositionalParamsMethodResolver positionalParamsMethodResolver = mock(PositionalParamsMethodResolver.class);
        NamedParamsMethodResolver namedParamsMethodResolver = mock(NamedParamsMethodResolver.class);
        OverloadedMethodDispatcher overloadedMethodDispatcher = new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, null);

        Object result = overloadedMethodDispatcher.dispatch(MissingNode.getInstance());

        assertEquals(EXPECTED_RESULT, result);
    }
}