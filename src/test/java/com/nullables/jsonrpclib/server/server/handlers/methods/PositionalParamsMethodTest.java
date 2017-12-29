package com.nullables.jsonrpclib.server.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nullables.jsonrpclib.server.exceptions.InvalidParamsException;
import com.nullables.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import com.nullables.jsonrpclib.server.handlers.methods.PositionalParamsMethod;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PositionalParamsMethodTest {

    @Test(expected = IllegalArgumentException.class)
    public void ctor_shouldThrow_whenMandatoryParamsCountIsLessThanZero() {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        new PositionalParamsMethod(-1, 1, jsonMethodAdapter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ctor_shouldThrow_whenOptionalParamsCountIsLessThanZero() {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        new PositionalParamsMethod(1, -1, jsonMethodAdapter);
    }

    @Test
    public void ctor_shouldCreateInstance() {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        new PositionalParamsMethod(1, 1, jsonMethodAdapter);
    }

    @Test
    public void invoke_shouldReturnResult() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        TextNode paramValue = new TextNode("abc");
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add(paramValue);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(new JsonNode[] { paramValue })).thenReturn(EXPECTED_RESULT);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(1, 0, jsonMethodAdapter);

        Object result = positionalParamsMethod.invoke(params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test
    public void invoke_shouldReturnResult_whenPassingOnlyMandatoryParams() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        TextNode paramValue = new TextNode("abc");
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add(paramValue);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(new JsonNode[] { paramValue, MissingNode.getInstance(), MissingNode.getInstance() })).thenReturn(EXPECTED_RESULT);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(1, 2, jsonMethodAdapter);

        Object result = positionalParamsMethod.invoke(params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test
    public void invoke_shouldReturnResult_whenPassingMoreParamsThanDefined() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        TextNode paramValue = new TextNode("abc");
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add(paramValue)
                .add(paramValue)
                .add(paramValue);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(new JsonNode[] { paramValue, paramValue })).thenReturn(EXPECTED_RESULT);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(1, 1, jsonMethodAdapter);

        Object result = positionalParamsMethod.invoke(params);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void invoke_shouldThrow_whenPassingLessParameterThanMandatory() throws Exception {
        TextNode paramValue = new TextNode("abc");
        ArrayNode params = new ObjectMapper()
                .createArrayNode()
                .add(paramValue);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(2, 0, jsonMethodAdapter);

        positionalParamsMethod.invoke(params);
    }

    @Test
    public void getParamsCount_shouldReturnParamsCount() {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(1, 1, jsonMethodAdapter);

        int paramsCount = positionalParamsMethod.getParamsCount();

        assertEquals(2, paramsCount);
    }

    @Test
    public void getMandatoryParamsCount_shouldReturnParamsCount() {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        PositionalParamsMethod positionalParamsMethod = new PositionalParamsMethod(9, 1, jsonMethodAdapter);

        int paramsCount = positionalParamsMethod.getMandatoryParamsCount();

        assertEquals(9, paramsCount);
    }
}