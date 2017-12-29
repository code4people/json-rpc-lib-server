package com.pushpopsoft.jsonrpclib.server.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pushpopsoft.jsonrpclib.binding.MethodBinding;
import com.pushpopsoft.jsonrpclib.binding.info.MethodInfo;
import com.pushpopsoft.jsonrpclib.server.ServiceActivator;
import com.pushpopsoft.jsonrpclib.server.handlers.JsonRequestHandler;
import com.pushpopsoft.jsonrpclib.server.handlers.MethodParamsHandler;
import com.pushpopsoft.jsonrpclib.server.handlers.RequestHandler;
import com.pushpopsoft.jsonrpclib.server.handlers.ServiceActivatorImpl;
import com.pushpopsoft.jsonrpclib.server.handlers.dispatch.MethodDispatcher;
import com.pushpopsoft.jsonrpclib.server.handlers.errorresolving.DebugErrorDetailLevel;
import com.pushpopsoft.jsonrpclib.server.handlers.errorresolving.DebugErrorFactory;
import com.pushpopsoft.jsonrpclib.server.serialization.ParamsDeserializer;
import com.pushpopsoft.jsonrpclib.server.serialization.ResultSerializer;
import com.pushpopsoft.jsonrpclib.server.serialization.SerializationService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceActivatorFactory {

    private SerializationService systemSerializationService = SerializationService.createSystemInstance();

    public ServiceActivator create(
            List<MethodBinding<? extends MethodInfo>> methodBindings,
            ObjectMapper customObjectMapper,
            DebugErrorDetailLevel debugErrorDetailLevel) {

        DebugErrorFactory debugErrorFactory = new DebugErrorFactory(debugErrorDetailLevel, systemSerializationService);
        MethodParamsHandler methodParamsHandler = createMethodParamsHandler(methodBindings, customObjectMapper);
        RequestHandler requestHandler = new RequestHandler(methodParamsHandler, debugErrorFactory);
        JsonRequestHandler jsonRequestHandler = new JsonRequestHandler(systemSerializationService, debugErrorFactory, requestHandler);

        return new ServiceActivatorImpl(systemSerializationService, jsonRequestHandler, debugErrorFactory);
    }

    private MethodParamsHandler createMethodParamsHandler(List<MethodBinding<? extends MethodInfo>> methodBindings, ObjectMapper customObjectMapper) {
        ResultSerializer resultSerializer = new ResultSerializer(customObjectMapper);
        ParamsDeserializer paramsDeserializer = new ParamsDeserializer(customObjectMapper);
        JsonMethodAdapterFactory jsonMethodAdapterFactory = new JsonMethodAdapterFactory(paramsDeserializer, resultSerializer);
        MethodOverloadRouterFactory methodOverloadRouterFactory = new MethodOverloadRouterFactory(jsonMethodAdapterFactory);
        SingleArgumentMethodDispatcherFactory singleArgumentMethodDispatcherFactory = new SingleArgumentMethodDispatcherFactory(jsonMethodAdapterFactory);
        MethodDispatcherFactory methodDispatcherFactory = new MethodDispatcherFactory(methodOverloadRouterFactory, singleArgumentMethodDispatcherFactory);

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
