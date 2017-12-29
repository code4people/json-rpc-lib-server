package com.nullables.jsonrpclib.server.factories;

import com.nullables.jsonrpclib.binding.MethodBinding;
import com.nullables.jsonrpclib.binding.info.SingleArgumentMethodInfo;
import com.nullables.jsonrpclib.server.handlers.dispatch.SingleArgumentMethodDispatcher;
import com.nullables.jsonrpclib.server.handlers.methods.JsonMethodAdapter;

public class SingleArgumentMethodDispatcherFactory {

    private final JsonMethodAdapterFactory jsonMethodAdapterFactory;

    public SingleArgumentMethodDispatcherFactory(JsonMethodAdapterFactory jsonMethodAdapterFactory) {
        this.jsonMethodAdapterFactory = jsonMethodAdapterFactory;
    }

    public SingleArgumentMethodDispatcher create(MethodBinding<SingleArgumentMethodInfo> methodBinding) {
        JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(methodBinding.getMethodInfo(), methodBinding.getReceiverSupplier());
        return new SingleArgumentMethodDispatcher(jsonMethodAdapter);
    }
}
