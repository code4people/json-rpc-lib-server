package com.nullables.jsonrpclib.server.handlers.methods;

import com.nullables.jsonrpclib.server.exceptions.InternalErrorException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.WrongMethodTypeException;
import java.util.Objects;
import java.util.function.Supplier;

public class Method {
    private final Supplier receiverSupplier;
    private final MethodHandle methodHandle;

    public Method(Supplier receiverSupplier, MethodHandle methodHandle) {
        this.receiverSupplier = receiverSupplier;
        this.methodHandle = methodHandle;
    }

    public Object invoke(Object[] params) throws Throwable {
        Objects.requireNonNull(params, "'params' cannot be null");
        Object receiver;
        try {
            receiver = receiverSupplier.get();
        }
        catch (Throwable t) {
            throw new InternalErrorException("Exception was thrown during obtaining of 'receiver'", t);
        }
        if (receiver == null) {
            throw new InternalErrorException("receiver object cannot be null");
        }
        Object[] methodHandleArguments = new Object[1 + params.length];
        methodHandleArguments[0] = receiver;
        System.arraycopy(params, 0, methodHandleArguments, 1, params.length);
        try {
            return methodHandle.invokeWithArguments(methodHandleArguments);
        }
        catch (ClassCastException | WrongMethodTypeException ex) {
            throw new InternalErrorException("Failed to call method.", ex);
        }
    }
}
