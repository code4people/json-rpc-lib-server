package com.code4people.jsonrpclib.server.handlers.errorresolving;

import com.code4people.jsonrpclib.server.serialization.SerializationException;
import com.code4people.jsonrpclib.server.serialization.SerializationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DebugErrorFactoryTest {
    @Test
    public void create_shouldReturnSerializedDebugErrorData_whenDebugErrorDetailIsSetToSTACKTRACE() throws Exception {
        TextNode expectedResult = TextNode.valueOf("result");
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(DebugErrorDetailLevel.STACKTRACE, serializationService);
        when(serializationService.serialize(any(DebugErrorFactory.DebugErrorData.class))).thenReturn(expectedResult);

        JsonNode data = debugErrorFactory.create("detail detailMessage", new RuntimeException());

        assertEquals(expectedResult, data);
    }

    @Test
    public void create_shouldReturnSimpleTextNodeErrorMessage_whenDebugErrorDataFailsToDeserialize() throws Exception {
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(DebugErrorDetailLevel.STACKTRACE, serializationService);
        when(serializationService.serialize(any(DebugErrorFactory.DebugErrorData.class))).thenThrow(new SerializationException("", null));

        JsonNode data = debugErrorFactory.create("detail detailMessage", new RuntimeException());

        assertEquals(TextNode.class, data.getClass());
    }

    @Test
    public void create_shouldReturnSerializedDebugErrorMessage_whenDebugErrorDetailIsSetToDETAIL_MESSAGE() throws Exception {
        TextNode expectedResult = TextNode.valueOf("result");
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(DebugErrorDetailLevel.DETAIL_MESSAGE, serializationService);
        when(serializationService.serialize(any(DebugErrorFactory.DebugErrorMessage.class))).thenReturn(expectedResult);

        JsonNode data = debugErrorFactory.create("detail detailMessage", new RuntimeException());

        assertEquals(expectedResult, data);
    }

    @Test
    public void create_shouldReturnMissingNode_whenDebugErrorDetailIsSetToNO_DETAIL() throws Exception {
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(DebugErrorDetailLevel.NO_DETAIL, serializationService);

        JsonNode data = debugErrorFactory.create("detail detailMessage", new RuntimeException());

        assertEquals(null, data);
    }
}