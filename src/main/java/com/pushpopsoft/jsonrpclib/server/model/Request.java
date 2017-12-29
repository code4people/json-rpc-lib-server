package com.pushpopsoft.jsonrpclib.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pushpopsoft.jsonrpclib.server.model.serialization.IdDeserializer;
import com.pushpopsoft.jsonrpclib.server.model.serialization.ParamsDeserializer;

public class Request {
    private final String jsonrpc;
    private final Object id;
    private final String method;
    private final JsonNode params;

    @JsonCreator
    public Request(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("method") String method,
            @JsonProperty("id")     @JsonDeserialize(using = IdDeserializer.class) Object id,
            @JsonProperty("params") @JsonDeserialize(using = ParamsDeserializer.class) JsonNode params) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public JsonNode getParams() {
        return params;
    }
}
