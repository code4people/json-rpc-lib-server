package com.nullables.jsonrpclib.server.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResultSerializer {
    private final ObjectMapper mapper;

    public ResultSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
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
