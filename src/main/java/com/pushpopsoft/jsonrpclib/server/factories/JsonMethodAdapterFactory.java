package com.pushpopsoft.jsonrpclib.server.factories;

import com.pushpopsoft.jsonrpclib.binding.BindingErrorException;
import com.pushpopsoft.jsonrpclib.binding.info.MethodInfo;
import com.pushpopsoft.jsonrpclib.server.handlers.errorresolving.MethodErrorMapping;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.JsonMethodAdapter;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.Method;
import com.pushpopsoft.jsonrpclib.server.serialization.ParamsDeserializer;
import com.pushpopsoft.jsonrpclib.server.serialization.ResultSerializer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class JsonMethodAdapterFactory {

    private final ParamsDeserializer paramsDeserializer;
    private final ResultSerializer resultSerializer;

    public JsonMethodAdapterFactory(ParamsDeserializer paramsDeserializer, ResultSerializer resultSerializer) {
        this.paramsDeserializer = paramsDeserializer;
        this.resultSerializer = resultSerializer;
    }

    public JsonMethodAdapter create(MethodInfo methodInfo, Supplier<?> receiverSupplier) {
        java.lang.reflect.Method reflectionMethod = methodInfo.getMethod();
        reflectionMethod.setAccessible(true);
        MethodHandle methodHandle;
        try {
            methodHandle = MethodHandles.lookup().unreflect(reflectionMethod);
        } catch (IllegalAccessException e) {
            throw new BindingErrorException("Error during MethodHandle creation.", e);
        }
        finally {
            reflectionMethod.setAccessible(false);
        }

        Method method = new Method(receiverSupplier, methodHandle);
        Type[] types = reflectionMethod.getGenericParameterTypes();

        MethodErrorMapping methodErrorMapping = new MethodErrorMapping(methodInfo.getErrorInfos());

        return new JsonMethodAdapter(
                method,
                types,
                reflectionMethod.getReturnType() == void.class,
                methodErrorMapping,
                paramsDeserializer,
                resultSerializer);
    }
}
