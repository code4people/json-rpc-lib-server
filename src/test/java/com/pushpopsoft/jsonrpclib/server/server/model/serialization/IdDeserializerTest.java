package com.pushpopsoft.jsonrpclib.server.server.model.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pushpopsoft.jsonrpclib.server.model.serialization.IdDeserializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdDeserializerTest {
    @Test
    public void deserialize_shouldReturnPositionalParams_whenPassingNumber() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("12345");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        int id = (int)idDeserializer.deserialize(parser, ctxt);

        assertEquals(12345, id);
    }

    @Test
    public void deserialize_shouldReturnPositionalParams_whenPassingFloatNumber() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("12345.6789");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        double id = (double)idDeserializer.deserialize(parser, ctxt);

        assertEquals(12345.6789, id, 0);
    }

    @Test
    public void deserialize_shouldReturnPositionalParams_whenPassingString() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("\"abcdefgh\"");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        String id = (String) idDeserializer.deserialize(parser, ctxt);

        assertEquals("abcdefgh", id);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingObject() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("{\"some\":\"param\"}");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        idDeserializer.deserialize(parser, ctxt);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingArray() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("[1,2,3]");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        idDeserializer.deserialize(parser, ctxt);
    }

    @Test(expected = JsonProcessingException.class)
    public void deserialize_shouldThrow_whenPassingboolean() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonParser parser = objectMapper.getFactory().createParser("true");
        DeserializationContext ctxt = objectMapper.getDeserializationContext();
        IdDeserializer idDeserializer = new IdDeserializer();
        parser.nextToken();

        idDeserializer.deserialize(parser, ctxt);
    }
}