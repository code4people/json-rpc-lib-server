package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.Optional;
import com.code4people.jsonrpclib.binding.annotations.Param;
import com.code4people.jsonrpclib.binding.annotations.ParamsType;
import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MissingParamsMethodTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void missingParamsMethod_shouldReturnResponse_whenBindWithOmittedParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"omittedParamsTypeMethod\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"pong1\"}", response);
    }

    @Test
    public void missingParamsMethod_shouldReturnResponse_whenBindWithNamedParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"namedParamsTypeMethod\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"pong2\"}", response);
    }

    @Test
    public void missingParamsMethod_shouldReturnResponse_whenBindWithPositionalParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"positionalParamsTypeMethod\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"pong3\"}", response);
    }


    public static class Receiver {
        @Bind
        public String omittedParamsTypeMethod() {
            return "pong1";
        }

        @Bind(paramsType = ParamsType.NAMED)
        public String namedParamsTypeMethod(@Param("s") @Optional String s) {
            return "pong2";
        }

        @Bind(paramsType = ParamsType.POSITIONAL)
        public String positionalParamsTypeMethod(@Optional String s) {
            return "pong3";
        }
    }
}
