package com.code4people.jsonrpclib.server.server.serialization;

import com.code4people.jsonrpclib.server.model.Request;
import com.code4people.jsonrpclib.server.serialization.SerializationException;
import com.code4people.jsonrpclib.server.serialization.SerializationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationServiceTest {
    @Test
    public void deserialize_shouldReturnResult() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Request EXPECTED_RESULT = new Request("2.0", "methodName", "1", objectMapper.createArrayNode().add("param1"));
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("jsonrpc", "2.0");
        objectNode.put("id", "1");
        objectNode.put("method", "methodName");
        objectNode.putArray("params")
            .add("param1");
        SerializationService serializationService = new SerializationService(objectMapper);

        Request result = serializationService.deserialize(objectNode);

        assertEquals(EXPECTED_RESULT.getId(), result.getId());
        assertEquals(EXPECTED_RESULT.getJsonrpc(), result.getJsonrpc());
        assertEquals(EXPECTED_RESULT.getMethod(), result.getMethod());
        assertEquals(EXPECTED_RESULT.getParams(), result.getParams());
    }

    @Test(expected = SerializationException.class)
    public void deserialize_shouldThrow_whenSerializationFails() throws Exception {
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("unknownField", "value");
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        serializationService.deserialize(objectNode);
    }

    @Test
    public void deserializeToJsonNode_shouldReturnResult() throws Exception {
        ObjectNode expectedResult = new ObjectMapper().createObjectNode();
        expectedResult.put("field", "value");
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        JsonNode result = serializationService.deserializeToJsonNode("{\"field\":\"value\"}");

        assertEquals(expectedResult, result);
    }

    @Test(expected = SerializationException.class)
    public void deserializeToJsonNode_shouldThrow_whenSerializationFails() throws Exception {
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        serializationService.deserializeToJsonNode("{\"field\":\"value\"");
    }

    @Test
    public void serializeToString_shouldReturnResult() throws Exception {
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        String result = serializationService.serializeToString(new C("value"));

        assertEquals("{\"field\":\"value\"}", result);
    }

    @Test(expected = SerializationException.class)
    public void serializeToString_shouldThrow_whenSerializationFails() throws Exception {
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        class D {

        }

        serializationService.serializeToString(new D());
    }

    @Test
    public void serialize_shouldReturnResult() throws Exception {
        ObjectNode expectedResult = new ObjectMapper().createObjectNode();
        expectedResult.put("field", "value");
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        JsonNode result = serializationService.serialize(new C("value"));

        assertEquals(expectedResult, result);
    }

    @Test(expected = SerializationException.class)
    public void serialize_shouldThrow_whenSerializationFails() throws Exception {
        SerializationService serializationService = new SerializationService(new ObjectMapper());

        class D {

        }

        serializationService.serialize(new D());
    }

    public static class C {
        public String field;

        public C() {
        }

        public C(String field) {
            this.field = field;
        }
    }
}