package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorDetailLevel;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.ParamsType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DebugErrorDetailLevelTest {
    @Test
    public void errorResponse_shouldHaveDetailMessage_whenDebugErrorDetailLevelIsSetToDETAIL_MESSAGE() {
        ServiceActivator serviceActivator = ServiceActivatorBuilder
                .create()
                .debugErrorDetailLevel(DebugErrorDetailLevel.DETAIL_MESSAGE)
                .register(Receiver.class, Receiver::new)
                .build();

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"params\": [false]," +
                "   \"id\": 3" +
                "}";

        String result = serviceActivator.processMessage(message).get();

        assertEquals(
                "{\"jsonrpc\":\"2.0\",\"id\":3,\"error\":{\"code\":-32602,\"message\":\"Invalid params\",\"data\":{\"detailMessage\":\"Failed to deserialize parameter value: 'false'.\"}}}",
                result);
    }

    @Test
    public void errorResponse_shouldHaveDetailMessage_whenDebugErrorDetailLevelIsSetToSTACKTRACE() {
        ServiceActivator serviceActivator = ServiceActivatorBuilder
                .create()
                .debugErrorDetailLevel(DebugErrorDetailLevel.STACKTRACE)
                .register(Receiver.class, Receiver::new)
                .build();

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"," +
                "   \"params\": [\"value\"]," +
                "   \"id\": 3" +
                "}";

        String result = serviceActivator.processMessage(message).get();

        assertTrue(result.contains("Failed to deserialize parameter value: '\\\"value\\\"'."));
        assertTrue(result.contains("\"exception\":"));
    }

    public static class Receiver {
        @Bind
        public void method(int a) {

        }
    }
}
