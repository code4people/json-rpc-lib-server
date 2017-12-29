package com.nullables.jsonrpclib.server.integrationtests;

import com.nullables.jsonrpclib.server.ServiceActivatorBuilder;
import com.nullables.jsonrpclib.binding.annotations.Bind;
import com.nullables.jsonrpclib.binding.annotations.Param;
import com.nullables.jsonrpclib.server.ServiceActivator;
import org.junit.Before;
import org.junit.Test;

import static com.nullables.jsonrpclib.binding.annotations.ParamsType.NAMED;
import static org.junit.Assert.assertEquals;

public class NamedParamsMethodTest {

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
        @Bind(paramsTypes = { NAMED })
        public int subtract(@Param("minuend") int minuend, @Param("subtrahend") int subtrahend) {
            return minuend - subtrahend;
        }
    }
}
