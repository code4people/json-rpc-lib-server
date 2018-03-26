package com.code4people.jsonrpclib.server.bindings;

import com.code4people.jsonrpclib.binding.info.MethodInfo;
import com.code4people.jsonrpclib.binding.info.SingleArgumentMethodInfo;

import java.util.function.Supplier;

public class SingleArgumentMethodBinding extends MethodBinding {
    public SingleArgumentMethodBinding(SingleArgumentMethodInfo methodInfo, Supplier<?> receiverSupplier) {
        super(methodInfo, receiverSupplier);
    }

    @Override
    public SingleArgumentMethodInfo getMethodInfo() {
        return (SingleArgumentMethodInfo) super.getMethodInfo();
    }
}
