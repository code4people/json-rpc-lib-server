package com.code4people.jsonrpclib.server.bindings;

import com.code4people.jsonrpclib.binding.info.MethodInfo;

import java.util.function.Supplier;

public abstract class MethodBinding {
    private final MethodInfo methodInfo;
    private final Supplier<?> receiverSupplier;

    public MethodBinding(MethodInfo methodInfo, Supplier<?> receiverSupplier) {
        this.methodInfo = methodInfo;
        this.receiverSupplier = receiverSupplier;
    }

    public MethodInfo getMethodInfo() {
        return methodInfo;
    }

    public Supplier<?> getReceiverSupplier() {
        return receiverSupplier;
    }
}
