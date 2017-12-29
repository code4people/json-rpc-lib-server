package com.nullables.jsonrpclib.server.handlers.methods;

public interface ReceiverSupplier<T> {
    T get() throws Throwable;
}
