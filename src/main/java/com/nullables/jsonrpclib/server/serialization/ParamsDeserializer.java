package com.nullables.jsonrpclib.server.serialization;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;

public class ParamsDeserializer {
    private final ObjectMapper mapper;
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public ParamsDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T deserialize(JsonNode jsonNode, Type type) throws SerializationException {
        try {
            JavaType javaType = typeFactory.constructType(type);
            return mapper.convertValue(jsonNode, javaType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Deserialization failed. json: '%s', type: '%s'", jsonNode, type);
            throw new SerializationException(message, e);
        }
    }
}
