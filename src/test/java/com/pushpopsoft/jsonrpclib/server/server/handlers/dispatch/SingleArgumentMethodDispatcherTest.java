package com.pushpopsoft.jsonrpclib.server.server.handlers.dispatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.pushpopsoft.jsonrpclib.server.handlers.dispatch.SingleArgumentMethodDispatcher;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleArgumentMethodDispatcherTest {
    @Test
    public void dispatch_shouldReturnResult() throws Exception {
        TextNode EXPECTED_RESULT = TextNode.valueOf("result");
        ArrayNode params = new ObjectMapper().createArrayNode();
        JsonMethodAdapter jsonMethodAdapter = mock(JsonMethodAdapter.class, Mockito.RETURNS_SMART_NULLS);
        when(jsonMethodAdapter.invoke(new JsonNode[] { params })).thenReturn(EXPECTED_RESULT);
        SingleArgumentMethodDispatcher singlePayloadMethodRouter = new SingleArgumentMethodDispatcher(jsonMethodAdapter);

        JsonNode jsonNode = singlePayloadMethodRouter.dispatch(params);

        assertEquals(EXPECTED_RESULT, jsonNode);
    }
}