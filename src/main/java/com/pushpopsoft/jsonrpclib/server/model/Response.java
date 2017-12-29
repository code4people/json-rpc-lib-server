package com.pushpopsoft.jsonrpclib.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import static com.pushpopsoft.jsonrpclib.server.model.ResponseError.*;

@JsonPropertyOrder({ "jsonrpc", "id", "result", "error" })
public class Response {
    private final String jsonrpc = "2.0";
    private final Object id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final JsonNode result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final ResponseError error;

    public static Response createParseError(JsonNode data) {
        return new Response(null, new ResponseError(PARSE_ERROR_CODE,"Parse error", data));
    }

    public static Response createInvalidRequestError(JsonNode data) {
        return new Response(null, new ResponseError(INVALID_REQUEST_CODE,"Invalid Request", data));
    }

    public static Response createInvalidParamsError(Object id, JsonNode data) {
        return new Response(id, new ResponseError(INVALID_PARAMS_CODE,"Invalid params", data));
    }

    public static Response createInternalError(Object id, JsonNode data) {
        return new Response(id, new ResponseError(INTERNAL_ERROR_CODE,"Internal error", data));
    }

    public static Response createMethodNotFoundError(Object id, JsonNode data) {
        return new Response(id, new ResponseError(METHOD_NOT_FOUND_CODE,"Method not found", data));
    }

    public static Response createError(Object id, int errorCode, String message, JsonNode data) {
        return new Response(id, new ResponseError(errorCode, message, data));
    }

    public static Response create(Object id, JsonNode result) {
        result = result == MissingNode.getInstance() ? null : result;
        return new Response(id, result);
    }

    public static Response create(Object id, ResponseError error) {
        return new Response(id, error);
    }

    @JsonCreator
    public static Response create(
            @JsonProperty("id") Object id,
            @JsonProperty("result") JsonNode result,
            @JsonProperty("error") ResponseError error) {
        return error == null ? new Response(id, result) : new Response(id, error);
    }

    private Response(Object id, ResponseError error) {
        this.id = id;
        this.result = null;
        this.error = error;
    }

    private Response(Object id, JsonNode result) {
        this.id = id;
        this.result = result;
        this.error = null;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public JsonNode getResult() {
        return result;
    }

    public ResponseError getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (id != null ? !id.equals(response.id) : response.id != null) return false;
        if (result != null ? !result.equals(response.result) : response.result != null) return false;
        return error != null ? error.equals(response.error) : response.error == null;
    }

    @Override
    public int hashCode() {
        int result1 = jsonrpc.hashCode();
        result1 = 31 * result1 + (id != null ? id.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (error != null ? error.hashCode() : 0);
        return result1;
    }
}
