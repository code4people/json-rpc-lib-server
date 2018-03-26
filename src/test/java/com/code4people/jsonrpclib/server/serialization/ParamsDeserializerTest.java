package com.code4people.jsonrpclib.server.serialization;

import com.code4people.jsonrpclib.server.model.serialization.ParamsDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParamsDeserializerTest {
    @Test
    public void deserialize_shouldReturnPositionalParams_whenPassingJsonArray() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode expectedResult = objectMapper.createArrayNode().add(1).add(2);
        JsonParser parser = objectMapper.getFactory().createParser("[1,2]");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        ArrayNode params = (ArrayNode) paramsDeserializer.deserialize(parser, ctxt);

        assertEquals(expectedResult, params);
    }

    @Test
    public void deserialize_shouldReturnNamedParams_whenPassingJsonObject() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode expectedResult = objectMapper.createObjectNode().put("param", "xyz");
        JsonParser parser = objectMapper.getFactory().createParser("{\"param\":\"xyz\"}");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        ObjectNode params = (ObjectNode) paramsDeserializer.deserialize(parser, ctxt);

        assertEquals(expectedResult, params);
    }

    @Test
    public void deserialize_shouldReturnNullNode_whenPassingNull() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("null");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        JsonNode params = paramsDeserializer.deserialize(parser, ctxt);

        assertEquals(NullNode.getInstance(), params);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingNumber() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("9");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        paramsDeserializer.deserialize(parser, ctxt);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingBoolean() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("true");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        paramsDeserializer.deserialize(parser, ctxt);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingString() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("\"some string\"");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer();
        parser.nextToken();

        paramsDeserializer.deserialize(parser, ctxt);
    }
}