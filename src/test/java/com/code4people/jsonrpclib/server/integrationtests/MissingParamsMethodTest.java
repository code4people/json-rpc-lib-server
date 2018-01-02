package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.server.ServiceActivator;
import org.junit.Before;
import org.junit.Test;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.MISSING;
import static org.junit.Assert.assertEquals;

public class MissingParamsMethodTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void missingParamsMethod_shouldReturnResponse() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"ping\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"pong\"}", response);
    }

    public static class Receiver {
        @Bind(paramsTypes = MISSING)
        public String ping() {
            return "pong";
        }
    }
}
