package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.Param;
import org.junit.Before;
import org.junit.Test;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.DEFAULT;
import static com.code4people.jsonrpclib.binding.annotations.ParamsType.NAMED;
import static com.code4people.jsonrpclib.binding.annotations.ParamsType.POSITIONAL;
import static org.junit.Assert.assertEquals;

public class NamedParamsMethodTest {

    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void namedParamsMethod_shouldReturnResponse_whenBindWithOmittedParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"omittedParamsTypeMethod\"," +
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

    @Test
    public void namedParamsMethod_shouldReturnResponse_whenBindWithDefaultParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"defaultParamsTypeMethod\"," +
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

    @Test
    public void namedParamsMethod_shouldReturnResponse_whenBindWithNamedParamsType() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"namedParamsTypeMethod\"," +
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

    @Test
    public void namedParamsMethod_shouldReturnError_whenMethodParamAnnotationIsMissing() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"missingParamAnnotation\"," +
                "   \"params\": " +
                "      {" +
                "          \"s\": \"s\"" +
                "      }," +
                "    \"id\": 3" +
                "}";
        ServiceActivator messageActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = messageActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}", response);
    }

    @Test
    public void namedParamsMethod_shouldReturnError_whenMethodIsNotBindAsNamedParamsMethod() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"notBindAsNamedParamMethod\"," +
                "   \"params\": " +
                "      {" +
                "          \"s\": \"s\"" +
                "      }," +
                "    \"id\": 3" +
                "}";
        ServiceActivator messageActivator = builder.register(Receiver.class, Receiver::new).build();
        String response = messageActivator.processMessage(message).get();

        assertEquals("{\"jsonrpc\":\"2.0\",\"id\":3,\"error\":{\"code\":-32602,\"message\":\"Invalid params\"}}", response);
    }

    public static class Receiver {
        @Bind
        public int omittedParamsTypeMethod(@Param("minuend") int minuend, @Param("subtrahend") int subtrahend) {
            return minuend - subtrahend;
        }

        @Bind(paramsType = DEFAULT)
        public int defaultParamsTypeMethod(@Param("minuend") int minuend, @Param("subtrahend") int subtrahend) {
            return minuend - subtrahend;
        }

        @Bind(paramsType = NAMED)
        public int namedParamsTypeMethod(@Param("minuend") int minuend, @Param("subtrahend") int subtrahend) {
            return minuend - subtrahend;
        }

        @Bind
        public void missingParamAnnotation(String s) {

        }

        @Bind(paramsType = POSITIONAL)
        public void notBindAsNamedParamMethod(String s) {

        }
    }
}
