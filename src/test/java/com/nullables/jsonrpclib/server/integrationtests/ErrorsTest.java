package com.nullables.jsonrpclib.server.integrationtests;

import com.nullables.jsonrpclib.server.ServiceActivatorBuilder;
import com.nullables.jsonrpclib.binding.annotations.Bind;
import com.nullables.jsonrpclib.binding.annotations.ErrorMapping;
import com.nullables.jsonrpclib.binding.annotations.ParamsType;
import com.nullables.jsonrpclib.server.ServiceActivator;
import com.nullables.jsonrpclib.server.exceptions.CustomErrorException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErrorsTest {

    private ServiceActivator serviceActivator;

    @Before
    public void setUp() throws Exception {
        serviceActivator = ServiceActivatorBuilder
                .create()
                .register(Receiver.class, Receiver::new)
                .build();
    }

    @Test
    public void callWithInvalidParams_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"method\", " +
                "   \"params\": {\"i\":1, \"j\":2}, " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}",
                response);
    }

    @Test
    public void callWithDeserializationFailure_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"method\", " +
                "   \"params\": [\"abc\", \"def\"], " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}",
                response);
    }

    @Test
    public void callWithInvalidParamsCount_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"method\", " +
                "   \"params\": [1], " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}",
                response);
    }

    @Test
    public void callForNonExistingMethod_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"foobar\", " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32601,\"message\":\"Method not found\"}}",
                response);
    }

    @Test
    public void callWithInvalidJson_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"foobar, " +
                "   \"params\": \"bar\", \"baz]";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32700,\"message\":\"Parse error\"}}",
                response);
    }

    @Test
    public void callWithInvalidRequest_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": 1," +
                "   \"params\": \"bar\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}",
                response);
    }

    @Test
    public void batchCallWithInvalidJson_shouldReturnError() {
        String message = "[\n" +
                "  {\"jsonrpc\": \"2.0\", \"method\": \"sum\", \"params\": [1,2,4], \"id\": \"1\"},\n" +
                "  {\"jsonrpc\": \"2.0\", \"method\"\n" +
                "]";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32700,\"message\":\"Parse error\"}}",
                response);
    }

    @Test
    public void emptyBatchCall_shouldReturnError() {
        String response = serviceActivator.processMessage("[]").get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}",
                response);
    }

    @Test
    public void batchCallWithInvalidRequest_shouldReturnBatchWithError() {
        String response = serviceActivator.processMessage("[1]").get();

        assertEquals(
                "[{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}]",
                response);
    }

    @Test
    public void batchCallWithInvalidRequests_shouldReturnBatchWithErrors() {
        String response = serviceActivator.processMessage("[1,2,4]").get();

        assertEquals(
                "[" +
                        "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}," +
                        "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}," +
                        "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}" +
                        "]",
                response);
    }

    @Test
    public void callThatResultsWithCustomError_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"methodWithCustomError\", " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32099,\"message\":\"This is custom error message.\",\"data\":{\"field\":\"value\"}}}",
                response);
    }

    @Test
    public void callThatResultsWithUnexpectedError_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"methodWithUnexpectedError\", " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32603,\"message\":\"Internal error\"}}",
                response);
    }

    @Test
    public void callThatResultsWithMappedError_shouldReturnError() {
        String message = "{" +
                "   \"jsonrpc\": \"2.0\", " +
                "   \"method\": \"methodWithMappedError\", " +
                "   \"id\": \"1\"" +
                "}";
        String response = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":32000,\"message\":\"This is custom message\"}}",
                response);
    }

    public static class Receiver {
        @Bind(paramsTypes = ParamsType.POSITIONAL)
        public String method(int i, int j) {
            return "result";
        }

        @Bind(paramsTypes = ParamsType.MISSING)
        public String methodWithCustomError() {
            throw new CustomErrorException(
                    -32099,
                    "This is custom error message.",
                    new Object() {
                        public String field = "value";
                    });
        }

        @Bind(paramsTypes = ParamsType.MISSING)
        public String methodWithUnexpectedError() {
            throw new RuntimeException("This is unexpected method message");
        }

        @Bind(paramsTypes = ParamsType.MISSING)
        @ErrorMapping(
                @com.nullables.jsonrpclib.binding.annotations.Error(code = 32000, exception = RuntimeException.class, message = "This is custom message")
        )
        public String methodWithMappedError() {
            throw new RuntimeException("This is unexpected method message");
        }
    }
}
