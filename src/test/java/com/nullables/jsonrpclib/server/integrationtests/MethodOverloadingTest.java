package com.nullables.jsonrpclib.server.integrationtests;

import com.nullables.jsonrpclib.server.ServiceActivator;
import com.nullables.jsonrpclib.server.ServiceActivatorBuilder;
import com.nullables.jsonrpclib.binding.annotations.Bind;
import com.nullables.jsonrpclib.binding.annotations.Optional;
import com.nullables.jsonrpclib.binding.annotations.Param;
import org.junit.Before;
import org.junit.Test;

import static com.nullables.jsonrpclib.binding.annotations.ParamsType.*;
import static org.junit.Assert.assertEquals;

public class MethodOverloadingTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    public static class NamedParamsOverloadingReceiver {
        @Bind(paramsTypes = NAMED)
        public String method(@Optional @Param("a") String a) {
            return "1";
        }
    }

    @Test
    public void method_shouldReturnResponse_whenSendingPositionalParams() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"params\": [\"value\"]," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"value\"}", response);
    }

    @Test
    public void method_shouldReturnResponse_whenSendingNameParams() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"params\": {\"param\": \"value\"}," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"value\"}", response);
    }

    @Test
    public void method_shouldReturnResponse_whenSendingNoParams() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":null}", response);
    }


    public static class Receiver {
        @Bind(paramsTypes = { MISSING, POSITIONAL, NAMED })
        public String method(@Optional @Param("param") String param) {
            return param;
        }
    }
}
