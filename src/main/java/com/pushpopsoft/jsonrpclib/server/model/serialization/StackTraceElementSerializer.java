package com.pushpopsoft.jsonrpclib.server.model.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StackTraceElementSerializer extends JsonSerializer<StackTraceElement> {
    public StackTraceElementSerializer() {
    }

    @Override
    public void serialize(StackTraceElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value == null ? "" : value.toString());
    }
}
