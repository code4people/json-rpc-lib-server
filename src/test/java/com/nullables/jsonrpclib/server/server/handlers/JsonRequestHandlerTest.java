package com.nullables.jsonrpclib.server.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.nullables.jsonrpclib.server.handlers.JsonRequestHandler;
import com.nullables.jsonrpclib.server.handlers.RequestHandler;
import com.nullables.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.nullables.jsonrpclib.server.model.Output;
import com.nullables.jsonrpclib.server.model.Request;
import com.nullables.jsonrpclib.server.model.Response;
import com.nullables.jsonrpclib.server.serialization.SerializationException;
import com.nullables.jsonrpclib.server.serialization.SerializationService;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JsonRequestHandlerTest {

    @Test
    public void processJsonNodeMessage_shouldReturnResponse_whenPassingSingleRequest() throws Exception {

        ObjectNode requestJson = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        Request request = mock(Request.class, RETURNS_SMART_NULLS);
        Response response = mock(Response.class, RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson)).thenReturn(request);
        when(requestHandler.processRequest(request)).thenReturn(Optional.of(response));
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Output output = jsonRequestHandler.processJsonNodeMessage(requestJson).get();

        assertFalse(output.isBatch());
        assertEquals(response, output.getSingle());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnEmptyOptional_whenRequestHandlerReturnsEmptyOptional() throws Exception {

        ObjectNode requestJson = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        Request request = mock(Request.class, RETURNS_SMART_NULLS);
        Response response = mock(Response.class, RETURNS_SMART_NULLS);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson)).thenReturn(request);
        when(requestHandler.processRequest(request)).thenReturn(Optional.empty());
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Optional<Output> result = jsonRequestHandler.processJsonNodeMessage(requestJson);

        assertFalse(result.isPresent());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnBatchResponse_whenPassingBatchRequests() throws Exception {

        ObjectNode requestJson1 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        ObjectNode requestJson2 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        Request request1 = mock(Request.class, RETURNS_SMART_NULLS);
        Request request2 = mock(Request.class, RETURNS_SMART_NULLS);
        Response response1 = mock(Response.class, RETURNS_SMART_NULLS);
        Response response2 = mock(Response.class, RETURNS_SMART_NULLS);
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        arrayNode.add(requestJson1);
        arrayNode.add(requestJson2);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson1)).thenReturn(request1);
        when(serializationService.deserialize(requestJson2)).thenReturn(request2);
        when(requestHandler.processRequest(request1)).thenReturn(Optional.of(response1));
        when(requestHandler.processRequest(request2)).thenReturn(Optional.of(response2));
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Output output = jsonRequestHandler.processJsonNodeMessage(arrayNode).get();

        assertTrue(output.isBatch());
        assertArrayEquals(new Response[] { response1, response2 }, output.getBatch());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnEmptyOptional_whenRequestHandlerReturnsEmptyOptionalForAllRequest() throws Exception {

        ObjectNode requestJson1 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        ObjectNode requestJson2 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        Request request1 = mock(Request.class, RETURNS_SMART_NULLS);
        Request request2 = mock(Request.class, RETURNS_SMART_NULLS);
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        arrayNode.add(requestJson1);
        arrayNode.add(requestJson2);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson1)).thenReturn(request1);
        when(serializationService.deserialize(requestJson2)).thenReturn(request2);
        when(requestHandler.processRequest(request1)).thenReturn(Optional.empty());
        when(requestHandler.processRequest(request2)).thenReturn(Optional.empty());
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Optional<Output> result = jsonRequestHandler.processJsonNodeMessage(arrayNode);

        assertFalse(result.isPresent());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnResponseWithError_whenPassingSingleInvalidRequest() throws Exception {

        ObjectNode requestJson = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        TextNode errorData = TextNode.valueOf("error data");
        Response response = Response.createInvalidRequestError(errorData);
        SerializationException serializationException = new SerializationException("", null);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson)).thenThrow(serializationException);
        when(debugErrorFactory.create("Deserialization of request json failed", serializationException)).thenReturn(errorData);
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Output output = jsonRequestHandler.processJsonNodeMessage(requestJson).get();

        assertFalse(output.isBatch());
        assertEquals(response, output.getSingle());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnBatchResponseWhereSomeHaveErrors_whenPassingRequestBatchWhereSomeAreInvalid() throws Exception {

        TextNode errorData = TextNode.valueOf("error data");
        ObjectNode requestJson1 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        ObjectNode requestJson2 = mock(ObjectNode.class, RETURNS_SMART_NULLS);
        Request request1 = mock(Request.class, RETURNS_SMART_NULLS);
        Response response1 = mock(Response.class, RETURNS_SMART_NULLS);
        Response response2 = Response.createInvalidRequestError(errorData);
        SerializationException serializationException = new SerializationException("", null);
        ArrayNode arrayNode = new ObjectMapper().createArrayNode();
        arrayNode.add(requestJson1);
        arrayNode.add(requestJson2);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(serializationService.deserialize(requestJson1)).thenReturn(request1);
        when(serializationService.deserialize(requestJson2)).thenThrow(serializationException);
        when(requestHandler.processRequest(request1)).thenReturn(Optional.of(response1));
        when(debugErrorFactory.create("Deserialization of request json failed", serializationException)).thenReturn(errorData);
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Output output = jsonRequestHandler.processJsonNodeMessage(arrayNode).get();

        assertTrue(output.isBatch());
        assertArrayEquals(new Response[] { response1, response2 }, output.getBatch());
    }

    @Test
    public void processJsonNodeMessage_shouldReturnSingleResponseWithError_whenPassingEmptyRequestBatch() {

        TextNode errorData = TextNode.valueOf("error data");
        ArrayNode emptyJsonArray = new ObjectMapper().createArrayNode();
        Request request = mock(Request.class, RETURNS_SMART_NULLS);
        Response response = Response.createInvalidRequestError(errorData);
        DebugErrorFactory debugErrorFactory = mock(DebugErrorFactory.class, RETURNS_SMART_NULLS);
        RequestHandler requestHandler = mock(RequestHandler.class, RETURNS_SMART_NULLS);
        SerializationService serializationService = mock(SerializationService.class, RETURNS_SMART_NULLS);
        when(requestHandler.processRequest(request)).thenReturn(Optional.of(response));
        when(debugErrorFactory.create("Request batch cannot be empty", null)).thenReturn(errorData);
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(serializationService, debugErrorFactory, requestHandler);

        Output output = jsonRequestHandler.processJsonNodeMessage(emptyJsonArray).get();

        assertFalse(output.isBatch());
        assertEquals(response, output.getSingle());
    }
}