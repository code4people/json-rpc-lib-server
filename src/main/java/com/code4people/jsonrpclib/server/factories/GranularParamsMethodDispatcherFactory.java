package com.code4people.jsonrpclib.server.factories;

import com.code4people.jsonrpclib.server.bindings.GranularParamsMethodBinding;
import com.code4people.jsonrpclib.server.handlers.dispatch.GranularParamsMethodDispatcher;
import com.code4people.jsonrpclib.server.handlers.methods.*;
import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.code4people.jsonrpclib.binding.info.PositionalParamsInfo;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.NamedParamsMethodResolver;
import com.code4people.jsonrpclib.server.handlers.dispatch.resolvers.PositionalParamsMethodResolver;

import java.util.List;
import java.util.stream.Collectors;

public class GranularParamsMethodDispatcherFactory {

    private final JsonMethodAdapterFactory jsonMethodAdapterFactory;

    public GranularParamsMethodDispatcherFactory(JsonMethodAdapterFactory jsonMethodAdapterFactory) {
        this.jsonMethodAdapterFactory = jsonMethodAdapterFactory;
    }

    public GranularParamsMethodDispatcher create(List<GranularParamsMethodBinding> granularParamsMethodBindings, String methodName) {
        List<GranularParamsMethodBinding> positionalParamsBindings = granularParamsMethodBindings
                .stream()
                .filter(b -> b.getMethodInfo().getPositionalParamsInfo() != null)
                .collect(Collectors.toList());
        List<PositionalParamsMethod> positionalParamsMethods = createPositionalParamsMethods(methodName, positionalParamsBindings);

        List<GranularParamsMethodBinding> namedParamsBindings = granularParamsMethodBindings.stream()
                .filter(b -> b.getMethodInfo().getNamedParamsInfo() != null)
                .collect(Collectors.toList());
        List<NamedParamsMethod> namedParamsMethods = createNamedParamsMethods(methodName, namedParamsBindings);

        List<GranularParamsMethodBinding> missingParamsBindings = granularParamsMethodBindings.stream()
                .filter(b -> b.getMethodInfo().getMissingParamsInfo() != null)
                .collect(Collectors.toList());
        MissingParamsMethod missingParamsMethod = createMissingParamsMethod(missingParamsBindings);

        NamedParamsMethodResolver namedParamsMethodResolver = new NamedParamsMethodResolver(namedParamsMethods);
        PositionalParamsMethodResolver positionalParamsMethodResolver = new PositionalParamsMethodResolver(positionalParamsMethods);
        return new GranularParamsMethodDispatcher(positionalParamsMethodResolver, namedParamsMethodResolver, missingParamsMethod);
    }

    private List<NamedParamsMethod> createNamedParamsMethods(String methodName, List<GranularParamsMethodBinding> methodBindings) {
        long numberOfBindingsWithUniqueSignature = methodBindings
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

        if (methodBindings.size() != numberOfBindingsWithUniqueSignature) {
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
            List<GranularParamsMethodBinding> methodBindings) {

        long numberOfBindingsWithUniqueSignature = methodBindings
                .stream()
                .map(x -> x.getMethodInfo().getPositionalParamsInfo().getParameterTypes().size())
                .distinct()
                .count();

        if (methodBindings.size() != numberOfBindingsWithUniqueSignature) {
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

    private MissingParamsMethod createMissingParamsMethod(List<GranularParamsMethodBinding> missingParamsBindings) {
        if (missingParamsBindings.isEmpty()) {
            return null;
        }
        else {
            GranularParamsMethodBinding methodBinding = missingParamsBindings.get(0);
            JsonMethodAdapter jsonMethodAdapter = jsonMethodAdapterFactory.create(methodBinding.getMethodInfo(), methodBinding.getReceiverSupplier());
            return new MissingParamsMethod(
                    jsonMethodAdapter,
                    methodBinding.getMethodInfo().getMissingParamsInfo().getNumberOfOptionalParams());
        }
    }
}
