package com.code4people.jsonrpclib.server.factories;

import com.code4people.jsonrpclib.server.handlers.dispatch.OverloadedMethodDispatcher;
import com.code4people.jsonrpclib.server.handlers.methods.*;
import com.code4people.jsonrpclib.server.handlers.methods.*;
import com.code4people.jsonrpclib.binding.*;
import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.code4people.jsonrpclib.binding.info.PositionalParamsInfo;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.NamedParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.PositionalParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.methods.*;

import java.util.List;
import java.util.stream.Collectors;

public class MethodOverloadRouterFactory {

    private final JsonMethodAdapterFactory jsonMethodAdapterFactory;

    public MethodOverloadRouterFactory(JsonMethodAdapterFactory jsonMethodAdapterFactory) {
        this.jsonMethodAdapterFactory = jsonMethodAdapterFactory;
    }

    public OverloadedMethodDispatcher create(List<MethodBinding<GranularParamsMethodInfo>> granularParamsMethodBindings, String methodName) {
        List<MethodBinding<GranularParamsMethodInfo>> positionalParamsBindings = granularParamsMethodBindings
                .stream()
                .filter(b -> b.getMethodInfo().getPositionalParamsInfo() != null)
                .collect(Collectors.toList());
        List<PositionalParamsMethod> positionalParamsMethods = createPositionalParamsMethods(methodName, positionalParamsBindings);

        List<MethodBinding<GranularParamsMethodInfo>> namedParamsBindings = granularParamsMethodBindings.stream()
                .filter(b -> b.getMethodInfo().getNamedParamsInfo() != null)
                .collect(Collectors.toList());
        List<NamedParamsMethod> namedParamsMethods = createNamedParamsMethods(methodName, namedParamsBindings);

        List<MethodBinding<GranularParamsMethodInfo>> missingParamsBindings = granularParamsMethodBindings.stream()
                .filter(b -> b.getMethodInfo().getMissingParamsInfo() != null)
                .collect(Collectors.toList());
        MissingParamsMethod missingParamsMethod = createMissingParamsMethod(methodName, missingParamsBindings);

        NamedParamsMethodResolver namedParamsMethodResolver = new NamedParamsMethodResolver(namedParamsMethods);
        PositionalParamsMethodResolver positionalParamsMethodResolver = new PositionalParamsMethodResolver(positionalParamsMethods);
        return new OverloadedMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
    }

    private List<NamedParamsMethod> createNamedParamsMethods(String methodName, List<MethodBinding<GranularParamsMethodInfo>> methodBindings) {
        long numberOfMethodsWithUniqueSignature = methodBindings
                .stream()
                .map(x -> x.getMethodInfo()
                        .getNamedParamsInfo()
                        .getParameters()
                        .stream()
                        .filter(parameter -> !parameter.optional)
                        .map(parameter -> parameter.name)
                        .collect(Collectors.toSet()))
                .distinct()
                .count();

        if (methodBindings.size() != numberOfMethodsWithUniqueSignature) {
            String message = String.format(
                    "Named parameters method '%s' is defined multiple times with the same set of mandatory parameters.",
                    methodName);

            throw new BindingErrorException(message);
        }

        return methodBindings
                .stream()
                .map(x -> {
                    GranularParamsMethodInfo methodInfo = x.getMethodInfo();
                    List<NamedParamDefinition> namedParamDefinitions = methodInfo
                            .getNamedParamsInfo()
                            .getParameters()
                            .stream()
                            .map(p -> new NamedParamDefinition(p.name, !p.optional))
                            .collect(Collectors.toList());

                    JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(methodInfo, x.getReceiverSupplier());

                    return new NamedParamsMethod(namedParamDefinitions, jsonMethodAdapter);
                })
                .collect(Collectors.toList());
    }

    private List<PositionalParamsMethod> createPositionalParamsMethods(
            String methodName,
            List<MethodBinding<GranularParamsMethodInfo>> methodBindings) {

        long numberOfMethodsWithUniqueSignature = methodBindings
                .stream()
                .map(x -> x.getMethodInfo().getPositionalParamsInfo().getParameterTypes().size())
                .distinct()
                .count();

        if (methodBindings.size() != numberOfMethodsWithUniqueSignature) {
            String message = String.format(
                    "Positional parameters method '%s' is defined multiple times with the same number of parameters.",
                    methodName);

            throw new BindingErrorException(message);
        }

        return methodBindings
                .stream()
                .map(x -> {
                    PositionalParamsInfo positionalParamsInfo = x.getMethodInfo().getPositionalParamsInfo();
                    int mandatoryParamsCount = positionalParamsInfo.getNumberOfMandatoryParams();
                    int optionalParamsCount = positionalParamsInfo.getParameterTypes().size() - mandatoryParamsCount;

                    JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(x.getMethodInfo(), x.getReceiverSupplier());

                    return new PositionalParamsMethod(
                            mandatoryParamsCount,
                            optionalParamsCount,
                            jsonMethodAdapter);
                })
                .collect(Collectors.toList());
    }

    private MissingParamsMethod createMissingParamsMethod(String methodName, List<MethodBinding<GranularParamsMethodInfo>> missingParamsBindings) {
        if (missingParamsBindings.isEmpty()) {
            return null;
        }
        else if (missingParamsBindings.size() > 1) {
            String message =
                    String.format(
                            "Method '%s' is exposed multiple times with paramsType = MISSING." +
                                    "At most one method with name '%s' can be defined with paramsType = MISSING," +
                                    "otherwise it would be ambiguous which one to processMethodParams in case of missing params.",
                            methodName,
                            methodName);
            throw new BindingErrorException(message);
        }
        else {
            MethodBinding<GranularParamsMethodInfo> methodBinding = missingParamsBindings.get(0);
            JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(methodBinding.getMethodInfo(), methodBinding.getReceiverSupplier());
            return new MissingParamsMethod(
                    jsonMethodAdapter,
                    methodBinding.getMethodInfo().getMissingParamsInfo().getNumberOfOptionalParams());
        }
    }
}
