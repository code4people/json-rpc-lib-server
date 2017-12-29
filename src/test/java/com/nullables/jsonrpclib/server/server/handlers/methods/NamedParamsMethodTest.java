package com.nullables.jsonrpclib.server.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nullables.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import com.nullables.jsonrpclib.server.handlers.methods.NamedParamDefinition;
import com.nullables.jsonrpclib.server.handlers.methods.NamedParamsMethod;
import com.nullables.jsonrpclib.server.exceptions.InvalidParamsException;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NamedParamsMethodTest {

    @Test
    public void invoke_shouldReturnResult() throws Exception {

        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        HashMap<String, JsonNode> namedParams = new HashMap<>();
        TextNode paramValue = new TextNode("abc");
        namedParams.put("param1", paramValue);
        NamedParamDefinition namedParamDefinition = new NamedParamDefinition("param1", false);
        List<NamedParamDefinition> namedParamsDefs = Arrays.asList(namedParamDefinition);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, RETURNS_SMART_NULLS);
        NamedParamsMethod namedParamsMethod = new NamedParamsMethod(namedParamsDefs, jsonMethodAdapter);
        when(jsonMethodAdapter.invoke(new JsonNode[] { paramValue })).thenReturn(EXPECTED_RESULT);

        JsonNode result = namedParamsMethod.invoke(namedParams);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test
    public void invoke_shouldRethrow_whenInvalidParamsExceptionIsThrown() throws Exception {

        HashMap<String, JsonNode> namedParams = new HashMap<>();
        TextNode paramValue = new TextNode("abc");
        namedParams.put("param1", paramValue);
        NamedParamDefinition namedParamDefinition = new NamedParamDefinition("param1", false);
        List<NamedParamDefinition> namedParamsDefs = Arrays.asList(namedParamDefinition);
        InvalidParamsException exception = new InvalidParamsException("detailMessage", null);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, RETURNS_SMART_NULLS);
        NamedParamsMethod namedParamsMethod = new NamedParamsMethod(namedParamsDefs, jsonMethodAdapter);
        when(jsonMethodAdapter.invoke(new JsonNode[] { paramValue })).thenThrow(exception);

        try {
            namedParamsMethod.invoke(namedParams);
        }
        catch (InvalidParamsException ex) {
            assertEquals(exception, ex);
        }
    }

    @Test
    public void invoke_shouldReturnResult_whenProvidingPartOfParams() throws Exception {

        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        HashMap<String, JsonNode> namedParams = new HashMap<>();
        TextNode paramValue = new TextNode("abc");
        namedParams.put("param2", paramValue);
        NamedParamDefinition namedParamDefinition1 = new NamedParamDefinition("param1", false);
        NamedParamDefinition namedParamDefinition2 = new NamedParamDefinition("param2", true);
        NamedParamDefinition namedParamDefinition3 = new NamedParamDefinition("param3", false);
        List<NamedParamDefinition> namedParamsDefs = Arrays.asList(namedParamDefinition1, namedParamDefinition2, namedParamDefinition3);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, RETURNS_SMART_NULLS);
        NamedParamsMethod namedParamsMethod = new NamedParamsMethod(namedParamsDefs, jsonMethodAdapter);
        when(jsonMethodAdapter.invoke(new JsonNode[] { MissingNode.getInstance(), paramValue, MissingNode.getInstance() })).thenReturn(EXPECTED_RESULT);

        JsonNode result = namedParamsMethod.invoke(namedParams);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = InvalidParamsException.class)
    public void invoke_shouldThrow_whenMandatoryParamIsMissing() throws Exception {

        HashMap<String, JsonNode> namedParams = new HashMap<>();
        NamedParamDefinition namedParamDefinition = new NamedParamDefinition("param1", true);
        List<NamedParamDefinition> namedParamsDefs = Arrays.asList(namedParamDefinition);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, RETURNS_SMART_NULLS);
        NamedParamsMethod namedParamsMethod = new NamedParamsMethod(namedParamsDefs, jsonMethodAdapter);

        namedParamsMethod.invoke(namedParams);
    }

    @Test
    public void getNamedParamsDefs_shouldReturnListOfNamedParamDef() {

        NamedParamDefinition namedParamDefinition = new NamedParamDefinition("param1", true);
        List<NamedParamDefinition> namedParamsDefs = Arrays.asList(namedParamDefinition);
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, RETURNS_SMART_NULLS);
        NamedParamsMethod namedParamsMethod = new NamedParamsMethod(namedParamsDefs, jsonMethodAdapter);

        List<NamedParamDefinition> result = namedParamsMethod.getNamedParamDefinitions();

        assertEquals(namedParamsDefs, result);
    }
}