package com.pushpopsoft.jsonrpclib.server.server.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.pushpopsoft.jsonrpclib.server.handlers.JsonRequestHandler;
import com.pushpopsoft.jsonrpclib.server.handlers.ServiceActivatorImpl;
import com.pushpopsoft.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.pushpopsoft.jsonrpclib.server.model.Output;
import com.pushpopsoft.jsonrpclib.server.model.Response;
import com.pushpopsoft.jsonrpclib.server.serialization.SerializationException;
import com.pushpopsoft.jsonrpclib.server.serialization.SerializationService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceActivatorImplTest {
    @Test
    public void processMessage_shouldReturnResponse() throws Exception {
        String inputJson = "inputJson";
        String outputJson = "outputJson";
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        Output output = mock(Output.class, Mockito.RETURNS_SMART_NULLS);
        Response response = mock(Response.class, Mockito.RETURNS_SMART_NULLS);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(output.isBatch()).thenReturn(false);
        when(output.getSingle()).thenReturn(response);
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.of(output));
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);
        when(serializationService.serializeToString(response)).thenReturn(outputJson);

        String result = messageHandlerImpl.processMessage(inputJson).get();

        assertEquals(outputJson, result);
    }

    @Test
    public void processMessage_shouldReturnEmptyOptional_whenJsonRequestHandlerReturnsEmptyOptional() throws Exception {
        String inputJson = "inputJson";
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.empty());
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);

        Optional<String> result = messageHandlerImpl.processMessage(inputJson);

        assertFalse(result.isPresent());
    }

    @Test
    public void processMessage_shouldReturnBatch() throws Exception {
        String inputJson = "inputJson";
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        Output output = mock(Output.class, Mockito.RETURNS_SMART_NULLS);
        Response response1 = mock(Response.class, Mockito.RETURNS_SMART_NULLS);
        Response response2 = mock(Response.class, Mockito.RETURNS_SMART_NULLS);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(output.isBatch()).thenReturn(true);
        when(output.getBatch()).thenReturn(new Response[]{response1, response2});
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.of(output));
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);
        when(serializationService.serializeToString(response1)).thenReturn("outputJson1");
        when(serializationService.serializeToString(response2)).thenReturn("outputJson2");

        String outputJson = messageHandlerImpl.processMessage(inputJson).get();

        assertEquals("[outputJson1,outputJson2]", outputJson);
    }

    @Test
    public void processMessage_shouldReturnBatchContainingErrorResponse_whenResultFailsToSerialize() throws Exception {
        String inputJson = "inputJson";
        SerializationException serializationException = new SerializationException("", null);
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        Output output = mock(Output.class, Mockito.RETURNS_SMART_NULLS);
        Response response1 = mock(Response.class, Mockito.RETURNS_SMART_NULLS);
        Response response2 = Response.create(1, TextNode.valueOf("result"));
        TextNode errorData = TextNode.valueOf("error data");
        Response responseWithError = Response.createInternalError(1, errorData);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(output.isBatch()).thenReturn(true);
        when(output.getBatch()).thenReturn(new Response[]{response1, response2});
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.of(output));
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);
        when(serializationService.serializeToString(response1)).thenReturn("outputJson1");
        when(serializationService.serializeToString(response2)).thenThrow(serializationException);
        when(serializationService.serializeToString(responseWithError)).thenReturn("outputJson2");
        when(debugErrorFactory.create("Failed to deserialize response.", serializationException)).thenReturn(errorData);

        String outputJson = messageHandlerImpl.processMessage(inputJson).get();

        assertEquals("[outputJson1,outputJson2]", outputJson);
    }

    @Test
    public void processMessage_shouldReturnErrorResponse_whenResultFailsToSerialize() throws Exception {
        String inputJson = "inputJson";
        String outputJson = "outputJson";
        SerializationException serializationException = new SerializationException("", null);
        Response response = Response.create(1, TextNode.valueOf("result"));
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        Output output = mock(Output.class, Mockito.RETURNS_SMART_NULLS);
        TextNode errorData = TextNode.valueOf("error data");
        Response responseWithError = Response.createInternalError(1, errorData);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(output.isBatch()).thenReturn(false);
        when(output.getSingle()).thenReturn(response);
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.of(output));
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);
        when(serializationService.serializeToString(response)).thenThrow(serializationException);
        when(serializationService.serializeToString(responseWithError)).thenReturn(outputJson);
        when(debugErrorFactory.create("Failed to deserialize response.", serializationException)).thenReturn(errorData);

        String result = messageHandlerImpl.processMessage(inputJson).get();

        assertEquals(outputJson, result);
    }

    @Test(expected = Error.class)
    public void processMessage_shouldThrowError_whenErrorResponseFailsToSerialize() throws Exception {
        String inputJson = "inputJson";
        SerializationException serializationException = new SerializationException("", null);
        Response response = Response.create(1, TextNode.valueOf("result"));
        JsonNode jsonNodeMessage = mock(JsonNode.class, Mockito.RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        Output output = mock(Output.class, Mockito.RETURNS_SMART_NULLS);
        TextNode errorData = TextNode.valueOf("error data");
        Response responseWithError = Response.createInternalError(1, errorData);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(output.isBatch()).thenReturn(false);
        when(output.getSingle()).thenReturn(response);
        when(jsonRequestHandler.processJsonNodeMessage(jsonNodeMessage)).thenReturn(Optional.of(output));
        when(serializationService.deserializeToJsonNode(inputJson)).thenReturn(jsonNodeMessage);
        when(serializationService.serializeToString(response)).thenThrow(serializationException);
        when(serializationService.serializeToString(responseWithError)).thenThrow(serializationException);
        when(debugErrorFactory.create("Failed to deserialize response.", serializationException)).thenReturn(errorData);

        messageHandlerImpl.processMessage(inputJson);
    }


    @Test
    public void processMessage_shouldReturnErrorResponse_whenMessageIsInvalidJson() throws Exception {
        String inputJson = "inputJson";
        String outputJson = "outputJson";
        SerializationException serializationException = new SerializationException("", null);
        SerializationService serializationService = mock(SerializationService.class, Mockito.RETURNS_SMART_NULLS);
        JsonRequestHandler jsonRequestHandler = mock(JsonRequestHandler.class, Mockito.RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, Mockito.RETURNS_SMART_NULLS);
        TextNode errorData = TextNode.valueOf("error data");
        Response responseWithError = Response.createParseError(errorData);
        ServiceActivatorImpl messageHandlerImpl = new ServiceActivatorImpl(serializationService, jsonRequestHandler, debugErrorFactory);
        when(serializationService.deserializeToJsonNode(inputJson)).thenThrow(serializationException);
        when(serializationService.serializeToString(responseWithError)).thenReturn(outputJson);
        when(debugErrorFactory.create(null, serializationException)).thenReturn(errorData);

        String result = messageHandlerImpl.processMessage(inputJson).get();

        assertEquals(outputJson, result);
    }
}