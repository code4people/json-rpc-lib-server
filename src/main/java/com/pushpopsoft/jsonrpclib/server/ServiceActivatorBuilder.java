package com.pushpopsoft.jsonrpclib.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pushpopsoft.jsonrpclib.binding.MethodBinding;
import com.pushpopsoft.jsonrpclib.binding.info.MethodInfo;
import com.pushpopsoft.jsonrpclib.server.factories.ServiceActivatorFactory;
import com.pushpopsoft.jsonrpclib.server.handlers.errorresolving.DebugErrorDetailLevel;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ServiceActivatorBuilder {

    private final ServiceActivatorFactory serviceActivatorFactory = new ServiceActivatorFactory();
    private final MethodBinding.Builder methodBindingBuilder = new MethodBinding.Builder();
    private DebugErrorDetailLevel debugErrorDetailLevel = DebugErrorDetailLevel.NO_DETAIL;
    private ObjectMapper objectMapper;

    public static ServiceActivatorBuilder create() {
        return new ServiceActivatorBuilder();
    }

    public <T> ServiceActivatorBuilder register(Class<T> receiver, Supplier<? extends T> supplier) {
        methodBindingBuilder.addReceiver(receiver, supplier);
        return this;
    }

    public ServiceActivatorBuilder debugErrorDetailLevel(DebugErrorDetailLevel debugErrorDetailLevel) {
        Objects.requireNonNull(debugErrorDetailLevel, "'debugErrorDetailLevel' cannot be null");
        this.debugErrorDetailLevel = debugErrorDetailLevel;
        return this;
    }

    public ServiceActivatorBuilder objectMapper(ObjectMapper objectMapper) {
        Objects.requireNonNull(objectMapper, "'objectMapper' cannot be null");
        this.objectMapper = objectMapper;
        return this;
    }

    public ServiceActivator build() {

        List<MethodBinding<? extends MethodInfo>> methodBindings = methodBindingBuilder.build();

        return serviceActivatorFactory.create(
                methodBindings,
                objectMapper == null ? new ObjectMapper() : objectMapper,
                debugErrorDetailLevel);
    }
}
