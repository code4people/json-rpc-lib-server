package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoidMethodTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void voidMethod_shouldReturnResponseWithoutResult() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"voidMethod\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3}", response);
    }

    public static class Receiver {
        @Bind(as = "voidMethod")
        public void method() {
        }
    }
}
