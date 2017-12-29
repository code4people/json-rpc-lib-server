package com.nullables.jsonrpclib.server.server.handlers.errorresolving;

import com.nullables.jsonrpclib.server.handlers.errorresolving.ServerError;
import com.nullables.jsonrpclib.binding.info.ErrorInfo;
import com.nullables.jsonrpclib.server.handlers.errorresolving.MethodErrorMapping;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MethodErrorMappingTest {
    @Test
    public void resolve_shouldReturnServerError() throws Exception {
        Map<Class<? extends Throwable>, ErrorInfo> map = new HashMap<>();
        map.put(RuntimeException.class, new ErrorInfo(9, "detailMessage"));
        MethodErrorMapping methodErrorMapping = new MethodErrorMapping(map);

        Optional<ServerError> result = methodErrorMapping.resolve(new RuntimeException());

        ServerError serverError = result.get();
        assertEquals(9, serverError.code);
        assertEquals("detailMessage", serverError.message);
    }

    @Test
    public void resolve_shouldReturnServerErrorWithMessageFromException_whenErrorInfoHasEmptyOrNullMessage() throws Exception {
        Map<Class<? extends Throwable>, ErrorInfo> map = new HashMap<>();
        map.put(RuntimeException.class, new ErrorInfo(9, ""));
        MethodErrorMapping methodErrorMapping = new MethodErrorMapping(map);

        Optional<ServerError> result = methodErrorMapping.resolve(new RuntimeException("detailMessage from exception"));

        ServerError serverError = result.get();
        assertEquals("detailMessage from exception", serverError.message);
    }

    @Test
    public void resolve_shouldReturnEmptyOptional_whenExceptionIsNotMapped() throws Exception {
        MethodErrorMapping methodErrorMapping = new MethodErrorMapping(Collections.emptyMap());

        Optional<ServerError> result = methodErrorMapping.resolve(new RuntimeException());

        assertFalse(result.isPresent());
    }
}