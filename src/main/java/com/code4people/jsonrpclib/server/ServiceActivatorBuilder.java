package com.code4people.jsonrpclib.server;

import com.code4people.jsonrpclib.binding.factories.GranularParamsMethodInfoFactory;
import com.code4people.jsonrpclib.binding.factories.MethodInfoFactory;
import com.code4people.jsonrpclib.binding.factories.SingleArgumentMethodInfoFactory;
import com.code4people.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.code4people.jsonrpclib.binding.info.SingleArgumentMethodInfo;
import com.code4people.jsonrpclib.server.bindings.GranularParamsMethodBinding;
import com.code4people.jsonrpclib.server.bindings.MethodBinding;
import com.code4people.jsonrpclib.server.bindings.SingleArgumentMethodBinding;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorDetailLevel;
import com.code4people.jsonrpclib.server.factories.ServiceActivatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServiceActivatorBuilder {

    private final ServiceActivatorFactory serviceActivatorFactory;
    private final MethodInfoFactory methodInfoFactory;
    private final Map<Class<?>, Supplier<?>> receivers = new HashMap<>();
    private DebugErrorDetailLevel debugErrorDetailLevel;
    private ObjectMapper objectMapper;

    public static ServiceActivatorBuilder create() {
        GranularParamsMethodInfoFactory granularParamsMethodInfoFactory = new GranularParamsMethodInfoFactory();
        SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory = new SingleArgumentMethodInfoFactory();
        MethodInfoFactory methodInfoFactory = new MethodInfoFactory(granularParamsMethodInfoFactory, singleArgumentMethodInfoFactory);
        return new ServiceActivatorBuilder(new ServiceActivatorFactory(), methodInfoFactory, DebugErrorDetailLevel.NO_DETAIL, new ObjectMapper());
    }

    ServiceActivatorBuilder(ServiceActivatorFactory serviceActivatorFactory, MethodInfoFactory methodInfoFactory, DebugErrorDetailLevel debugErrorDetailLevel, ObjectMapper objectMapper) {
        this.serviceActivatorFactory = serviceActivatorFactory;
        this.methodInfoFactory = methodInfoFactory;
        this.debugErrorDetailLevel = debugErrorDetailLevel;
        this.objectMapper = objectMapper;
    }

    public <T> ServiceActivatorBuilder register(Class<T> receiver, Supplier<? extends T> supplier) {
        Objects.requireNonNull(receiver, "'receiver' cannot be null");
        Objects.requireNonNull(supplier, "'supplier' cannot be null");
        receivers.put(receiver, supplier);
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

        List<MethodBinding> methodBindings = receivers
                .entrySet()
                .stream()
                .flatMap(cse -> methodInfoFactory.createFromClass(cse.getKey())
                        .stream()
                        .map(mi -> {
                            if (mi instanceof GranularParamsMethodInfo) {
                                return new GranularParamsMethodBinding((GranularParamsMethodInfo)mi, cse.getValue());
                            }
                            else if (mi instanceof SingleArgumentMethodInfo) {
                                return new SingleArgumentMethodBinding((SingleArgumentMethodInfo)mi, cse.getValue());
                            }
                            else {
                                throw new IllegalStateException();
                            }
                        }))
                .collect(Collectors.toList());

        return serviceActivatorFactory.create(
                methodBindings,
                objectMapper == null ? new ObjectMapper() : objectMapper,
                debugErrorDetailLevel);
    }
}
