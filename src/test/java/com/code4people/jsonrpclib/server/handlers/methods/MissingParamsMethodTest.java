package com.code4people.jsonrpclib.server.handlers.methods;

import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MissingParamsMethodTest {
    @Test
    public void invoke_shouldReturnResultAndPassMissingNodeParamsToJsonMethodAdapter() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        JsonNode[] params = {
                MissingNode.getInstance(),
                MissingNode.getInstance(),
                MissingNode.getInstance()
        };
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(params)).thenReturn(EXPECTED_RESULT);
        MissingParamsMethod missingParamsMethod = new MissingParamsMethod(jsonMethodAdapter, 3);

        JsonNode result = missingParamsMethod.invoke();

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = BaseErrorException.class)
    public void invoke_shouldThrow_whenJsonMethodAdapterThrows() throws Exception {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(new JsonNode[0])).thenThrow(new BaseErrorException(""));
        MissingParamsMethod missingParamsMethod = new MissingParamsMethod(jsonMethodAdapter, 0);

        missingParamsMethod.invoke();
    }
}