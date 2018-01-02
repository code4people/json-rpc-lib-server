package com.code4people.jsonrpclib.server.handlers.methods;

public interface ReceiverSupplier<T> {
    T get() throws Throwable;
}
