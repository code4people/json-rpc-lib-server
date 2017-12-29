package com.nullables.jsonrpclib.server.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nullables.jsonrpclib.server.model.Request;
import com.nullables.jsonrpclib.server.model.serialization.StackTraceElementSerializer;

import java.io.IOException;

public class SerializationService {
    private final ObjectMapper mapper;
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public SerializationService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public static SerializationService createCustomInstance(ObjectMapper mapper) {
        return new SerializationService(mapper);
    }

    public static SerializationService createSystemInstance() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(StackTraceElement.class, new StackTraceElementSerializer());
        mapper.registerModule(module);
        return new SerializationService(mapper);
    }

    public Request deserialize(JsonNode jsonNode) throws SerializationException {
        try {
            JavaType javaType = typeFactory.constructType(Request.class);
            return mapper.convertValue(jsonNode, javaType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Deserialization to Request failed. json: '%s'.", jsonNode);
            throw new SerializationException(message, e);
        }
    }

    public JsonNode deserializeToJsonNode(String json) throws SerializationException {
        try {
            return mapper.readTree(json);
        }
        catch (IOException e) {
            String message = String.format("Deserialization to JsonNode failed. json: '%s'", json);
            throw new SerializationException(message, e);
        }
    }

    public String serializeToString(Object object) throws SerializationException {
        try {
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            String message = String.format(
                    "Serialization failed. class: '%s'",
                    object == null ? "null object" : object.getClass());
            throw new SerializationException(message, e);
        }
    }

    public JsonNode serialize(Object object) throws SerializationException {
        try {
            return mapper.valueToTree(object);
        }
        catch (IllegalArgumentException e) {
            String message = String.format(
                    "Serialization failed. class: '%s'",
                    object == null ? "null object" : object.getClass());
            throw new SerializationException(message, e);
        }
    }
}
