package com.nullables.jsonrpclib.server.integrationtests;

import com.nullables.jsonrpclib.server.ServiceActivator;
import com.nullables.jsonrpclib.server.ServiceActivatorBuilder;
import com.nullables.jsonrpclib.binding.annotations.Bind;
import org.junit.Test;

import java.util.Optional;

import static com.nullables.jsonrpclib.binding.annotations.ParamsType.MISSING;
import static com.nullables.jsonrpclib.binding.annotations.ParamsType.POSITIONAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BatchRequestTest {
    @Test
    public void batchCall_shouldReturnBatch() {
        ServiceActivator serviceActivator = ServiceActivatorBuilder
                .create()
                .register(Receiver.class, Receiver::new)
                .build();
        String response = serviceActivator.processMessage("[" +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"sum\", \"params\": [1,2,4], \"id\": \"1\"}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [7]}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"subtract\", \"params\": [42,23], \"id\": \"2\"}," +
                "   {\"foo\": \"boo\"}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"foo.get\", \"params\": {\"name\": \"myself\"}, \"id\": \"5\"}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"get_data\", \"id\": \"9\"}" +
                "]").get();

        assertEquals("[" +
                "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":7}," +
                "{\"jsonrpc\":\"2.0\",\"id\":\"2\",\"result\":19}," +
                "{\"jsonrpc\":\"2.0\",\"id\":null,\"error\":{\"code\":-32600,\"message\":\"Invalid Request\"}}," +
                "{\"jsonrpc\":\"2.0\",\"id\":\"5\",\"error\":{\"code\":-32601,\"message\":\"Method not found\"}}," +
                "{\"jsonrpc\":\"2.0\",\"id\":\"9\",\"result\":[\"hello\",5]}" +
                "]", response);
    }

    @Test
    public void batchCall_shouldReturnEmptyoptional_whenAllRequestsAreNotifications() {
        ServiceActivator serviceActivator = ServiceActivatorBuilder
                .create()
                .register(Receiver.class, Receiver::new)
                .build();
        Optional<String> response = serviceActivator.processMessage("[\n" +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [7]}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [6]}," +
                "   {\"jsonrpc\": \"2.0\", \"method\": \"notify_hello\", \"params\": [5]}" +
                "]");

        assertFalse(response.isPresent());
    }

    public static class Receiver {
        @Bind(paramsTypes = POSITIONAL)
        public int sum(int a, int b, int c) {
            return a + b + c;
        }

        @Bind(paramsTypes = POSITIONAL)
        public void notify_hello(int a) {
        }

        @Bind(paramsTypes = POSITIONAL)
        public int subtract(int minuend, int subtrahend) {
            return minuend - subtrahend;
        }

        @Bind(paramsTypes = MISSING)
        public Object[] get_data() {
            return new Object[] { "hello", 5 };
        }
    }
}
