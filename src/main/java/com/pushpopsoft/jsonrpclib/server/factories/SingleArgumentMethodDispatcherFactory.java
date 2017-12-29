package com.pushpopsoft.jsonrpclib.server.factories;

import com.pushpopsoft.jsonrpclib.binding.MethodBinding;
import com.pushpopsoft.jsonrpclib.binding.info.SingleArgumentMethodInfo;
import com.pushpopsoft.jsonrpclib.server.handlers.dispatch.SingleArgumentMethodDispatcher;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.JsonMethodAdapter;

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
