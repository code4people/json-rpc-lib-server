package com.code4people.jsonrpclib.server.model.serialization;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class IdDeserializer extends StdDeserializer<Object> {

    public IdDeserializer() {
        super(Object.class);
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken currentToken = p.getCurrentToken();

        if (currentToken == JsonToken.VALUE_STRING
                || currentToken.isNumeric()
                || currentToken == JsonToken.VALUE_NULL
                || currentToken == JsonToken.NOT_AVAILABLE) {
            return p.readValueAs(Object.class);
        }
        else {
            // id could be only string, number, null or not available
            throw new InvalidIdException("Request id is of bad type. It can be string, number, null or missing.", p.getCurrentLocation());
        }
    }

    public static class InvalidIdException extends JsonProcessingException {
        public InvalidIdException(String msg, JsonLocation loc) {
            super(msg, loc);
        }
    }
}
