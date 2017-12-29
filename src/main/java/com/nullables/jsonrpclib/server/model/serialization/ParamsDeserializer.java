package com.nullables.jsonrpclib.server.model.serialization;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ParamsDeserializer extends StdDeserializer<JsonNode> {
    public ParamsDeserializer() {
        super(Object.class);
    }

    @Override
    public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        JsonToken currentToken = p.getCurrentToken();

        if (currentToken == JsonToken.START_ARRAY
                || currentToken == JsonToken.START_OBJECT
                || currentToken == JsonToken.NOT_AVAILABLE
                || currentToken == JsonToken.VALUE_NULL) {
            return p.readValueAs(JsonNode.class);
        }
        else {
            // invalid params object
            throw new InvalidParamsException("Request params can be only array, object, null or missing.", p.getCurrentLocation());
        }
    }

    public static class InvalidParamsException extends JsonProcessingException {
        public InvalidParamsException(String msg, JsonLocation loc) {
            super(msg, loc);
        }
    }
}
