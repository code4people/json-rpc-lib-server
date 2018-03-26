package com.code4people.jsonrpclib.server.factories;

import com.code4people.jsonrpclib.server.ServiceActivator;
import com.code4people.jsonrpclib.server.bindings.MethodBinding;
import com.code4people.jsonrpclib.server.handlers.JsonRequestHandler;
import com.code4people.jsonrpclib.server.handlers.MethodParamsHandler;
import com.code4people.jsonrpclib.server.handlers.RequestHandler;
import com.code4people.jsonrpclib.server.handlers.dispatch.MethodDispatcher;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorDetailLevel;
import com.code4people.jsonrpclib.server.serialization.ResultSerializer;
import com.code4people.jsonrpclib.server.serialization.SerializationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code4people.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.code4people.jsonrpclib.server.handlers.ServiceActivatorImpl;
import com.code4people.jsonrpclib.server.serialization.ParamsDeserializer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceActivatorFactory {

    private SerializationService systemSerializationService = SerializationService.createSystemInstance();

    public ServiceActivator create(
            List<MethodBinding> methodBindings,
            ObjectMapper customObjectMapper,
            DebugErrorDetailLevel debugErrorDetailLevel) {

        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(debugErrorDetailLevel, systemSerializationService);
        MethodParamsHandler methodParamsHandler = createMethodParamsHandler(methodBindings, customObjectMapper);
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(systemSerializationService, debugErrorFactory, requestHandler);

        return new ServiceActivatorImpl(systemSerializationService, jsonRequestHandler, debugErrorFactory);
    }

    private MethodParamsHandler createMethodParamsHandler(List<MethodBinding> methodBindings, ObjectMapper customObjectMapper) {
        ResultSerializer resultSerializer = new ResultSerializer(customObjectMapper);
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer(customObjectMapper);
        JsonMethodAdapterFactory jsonMethodAdapterFactory = new JsonMethodAdapterFactory(paramsDeserializer, resultSerializer);
        GranularParamsMethodDispatcherFactory granularParamsMethodDispatcherFactory = new GranularParamsMethodDispatcherFactory(jsonMethodAdapterFactory);
        SingleArgumentMethodDispatcherFactory singleArgumentMethodDispatcherFactory = new SingleArgumentMethodDispatcherFactory(jsonMethodAdapterFactory);
        MethodDispatcherFactory methodDispatcherFactory = new MethodDispatcherFactory(granularParamsMethodDispatcherFactory, singleArgumentMethodDispatcherFactory);

        Map<String, MethodDispatcher> methodDispatchersMap = methodBindings
                .stream()
                .collect(Collectors.groupingBy(x -> x.getMethodInfo().getPublicName()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> methodDispatcherFactory.create(e.getKey(), e.getValue())));

        return new MethodParamsHandler(methodDispatchersMap);
    }
}
