package com.code4people.jsonrpclib.server.factories;

import com.code4people.jsonrpclib.server.bindings.SingleArgumentMethodBinding;
import com.code4people.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import com.code4people.jsonrpclib.server.handlers.dispatch.SingleArgumentMethodDispatcher;

public class SingleArgumentMethodDispatcherFactory {

    private final JsonMethodAdapterFactory jsonMethodAdapterFactory;

    public SingleArgumentMethodDispatcherFactory(JsonMethodAdapterFactory jsonMethodAdapterFactory) {
        this.jsonMethodAdapterFactory = jsonMethodAdapterFactory;
    }

    public SingleArgumentMethodDispatcher create(SingleArgumentMethodBinding methodBinding) {
        JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(methodBinding.getMethodInfo(), methodBinding.getReceiverSupplier());
        return new SingleArgumentMethodDispatcher(jsonMethodAdapter);
    }
}
