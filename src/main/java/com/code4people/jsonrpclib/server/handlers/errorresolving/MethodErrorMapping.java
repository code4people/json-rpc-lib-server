package com.code4people.jsonrpclib.server.handlers.errorresolving;

import com.code4people.jsonrpclib.binding.info.ErrorInfo;

import java.util.Map;
import java.util.Optional;

public class MethodErrorMapping {
    private final Map<Class<? extends Throwable>, ErrorInfo> errorInfoMap;

    public MethodErrorMapping(Map<Class<? extends Throwable>, ErrorInfo> errorInfoMap) {
        this.errorInfoMap = errorInfoMap;
    }

    public Optional<ServerError> resolve(Throwable throwable) {
        if (errorInfoMap.containsKey(throwable.getClass())) {
            ErrorInfo errorInfo = errorInfoMap.get(throwable.getClass());
            String message = errorInfo.message;
            if (message == null || message.isEmpty()) {
                message = throwable.getMessage();
            }
            ServerError serverError = ServerError.of(errorInfo.code, message, null);
            return Optional.of(serverError);
        }
        else {
            return Optional.empty();
        }
    }
}
