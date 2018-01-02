package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.Optional;
import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.POSITIONAL;
import static org.junit.Assert.assertEquals;

public class PositionalParamsMethodTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void positionalParamsMethod_shouldReturnResponse() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"sum\"," +
                "   \"params\": [ 12, 16 ]," +
                "    \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":28}", response);
    }

    @Test
    public void positionalParamsMethod_shouldReturnResponse_whenOptionalParamIsNotPassed() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"substring\"," +
                "   \"params\": [ \"one two\", 4 ]," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(OptionalParamsReceiver.class, OptionalParamsReceiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"two\"}", response);
    }

    @Test
    public void positionalParamsMethod_shouldReturnResponse_whenOptionalParamIsPassed() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"substring\"," +
                "   \"params\": [ \"one two three\", 4, 7 ]," +
                "   \"id\": 3" +
                "}";
        ServiceActivator serviceActivator = builder.register(OptionalParamsReceiver.class, OptionalParamsReceiver::new).build();
        String response = serviceActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"result\":\"two\"}", response);
    }

    public static class OptionalParamsReceiver {
        @Bind(paramsTypes = POSITIONAL)
        public String substring(String text, int beginIndex, @Optional Integer endIndex) {
            return endIndex == null
                    ? text.substring(beginIndex)
                    : text.substring(beginIndex, endIndex);
        }
    }

    public static class Receiver {
        @Bind(paramsTypes = { POSITIONAL })
        public int sum(int a, int b) {
            return a + b;
        }
    }
}
