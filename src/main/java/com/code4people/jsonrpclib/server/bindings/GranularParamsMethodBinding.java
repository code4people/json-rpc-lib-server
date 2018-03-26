package com.code4people.jsonrpclib.server.bindings;

import com.code4people.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.code4people.jsonrpclib.binding.info.MethodInfo;

import java.util.function.Supplier;

public class GranularParamsMethodBinding extends MethodBinding {
    public GranularParamsMethodBinding(GranularParamsMethodInfo methodInfo, Supplier<?> receiverSupplier) {
        super(methodInfo, receiverSupplier);
    }

    @Override
    public GranularParamsMethodInfo getMethodInfo() {
        return (GranularParamsMethodInfo) super.getMethodInfo();
    }
}
