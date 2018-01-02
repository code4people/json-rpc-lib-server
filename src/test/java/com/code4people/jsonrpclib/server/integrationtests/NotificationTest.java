package com.code4people.jsonrpclib.server.integrationtests;

import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.ServiceActivatorBuilder;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.MISSING;
import static org.junit.Assert.assertFalse;

public class NotificationTest {
    private ServiceActivatorBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = ServiceActivatorBuilder
                .create();
    }

    @Test
    public void voidMethod_shouldReturnEmptyResponse() {

        String message = "{" +
                "   \"jsonrpc\": \"2.0\"," +
                "   \"method\": \"method\"" +
                "}";
        ServiceActivator serviceActivator = builder.register(Receiver.class, Receiver::new).build();
        Optional<String> response = serviceActivator.processMessage(message);

        assertFalse(response.isPresent());
    }

    public static class Receiver {
        @Bind(paramsTypes = MISSING)
        public String method() {
            return "result";
        }
    }
}
