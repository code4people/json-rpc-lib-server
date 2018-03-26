package com.code4people.jsonrpclib.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.code4people.jsonrpclib.server.exceptions.BaseErrorException;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleParamMethodTest {
    @Test
    public void invoke_shouldReturnResult() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        TextNode param = TextNode.valueOf("param");
        JsonNode[] params = { param };
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(params)).thenReturn(EXPECTED_RESULT);
        SingleParamMethod singleParamMethod = new SingleParamMethod(jsonMethodAdapter);

        JsonNode result = singleParamMethod.invoke(param);

        assertEquals(EXPECTED_RESULT, result);
    }

    @Test(expected = BaseErrorException.class)
    public void invoke_shouldThrow_whenJsonMethodAdapterThrows() throws Exception {
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(any())).thenThrow(new BaseErrorException(""));
        SingleParamMethod singleParamMethod = new SingleParamMethod(jsonMethodAdapter);

        singleParamMethod.invoke(TextNode.valueOf("param"));
    }
}