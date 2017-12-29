package com.pushpopsoft.jsonrpclib.server.integrationtests;

import com.pushpopsoft.jsonrpclib.binding.annotations.BindToSingleArgument;
import com.pushpopsoft.jsonrpclib.server.ServiceActivator;
import com.pushpopsoft.jsonrpclib.server.ServiceActivatorBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SingleArgumentMethodTest {
    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void namedParamsMethod_shouldReturnResponse() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"subtract\"," +
                "   \"params\": " +
                "      {" +
                "          \"subtrahend\": 23," +
                "          \"minuend\": 42}," +
                "    \"id\": 3" +
                "}";
        ServiceActivator messageActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = messageActivator.processMessage(message).get();
        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":19}", response);
    }

    public static class Receiver {
        @BindToSingleArgument
        public int subtract(Payload payload) {
            return payload.minuend - payload.subtrahend;
        }
    }

    public static class Payload {
        public int minuend;
        public int subtrahend;
    }
}
