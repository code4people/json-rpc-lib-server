package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.Param;
import com.code4people.jsonrpclib.binding.annotations.ParamsType;
import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SerializationTest {
    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = com.code4people.jsonrpclib.server.ServiceActivatorBuilder
                .create();
    }

    @Test
    public void method_shouldReturnResponse_whenParamIsGeneric() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"params\": {\"array\":[{\"field\":\"somestring\"}]}," +
                "    \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"somestring\"}", response);
    }

    public static class Receiver {
        @Bind
        public String method(@Param("array") List<Cart> array) {
            return array.get(0).field;
        }

        public static class Cart {
            public String field;
        }
    }

}
