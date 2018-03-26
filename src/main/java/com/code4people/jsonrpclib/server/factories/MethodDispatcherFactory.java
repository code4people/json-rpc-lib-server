package com.code4people.jsonrpclib.server.factories;

import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.server.bindings.GranularParamsMethodBinding;
import com.code4people.jsonrpclib.server.bindings.MethodBinding;
import com.code4people.jsonrpclib.server.bindings.SingleArgumentMethodBinding;
import com.code4people.jsonrpclib.server.handlers.dispatch.MethodDispatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodDispatcherFactory {

    private final GranularParamsMethodDispatcherFactory granularParamsMethodDispatcherFactory;
    private final SingleArgumentMethodDispatcherFactory singleArgumentMethodDispatcherFactory;

    public MethodDispatcherFactory(GranularParamsMethodDispatcherFactory granularParamsMethodDispatcherFactory, SingleArgumentMethodDispatcherFactory singleArgumentMethodDispatcherFactory) {
        this.granularParamsMethodDispatcherFactory = granularParamsMethodDispatcherFactory;
        this.singleArgumentMethodDispatcherFactory = singleArgumentMethodDispatcherFactory;
    }

    public MethodDispatcher create(String methodName, List<MethodBinding> methodBindings) {
        Optional<SingleArgumentMethodBinding> singleArgumentMethodBinding =
                methodBindings
                        .stream()
                        .filter(mb -> mb instanceof SingleArgumentMethodBinding)
                        .map(SingleArgumentMethodBinding.class::cast)
                        .findFirst();

        if (singleArgumentMethodBinding.isPresent()) {
            if (methodBindings.size() == 1) {
                return singleArgumentMethodDispatcherFactory.create(singleArgumentMethodBinding.get());
            }
            else {
                String message = String.format(
                        "Method name conflict was detected: '%s'." +
                        "Method overloading is not supported for methods with single argument binding.",
                        methodName);
                throw new BindingErrorException(message);
            }
        }
        else {
            List<GranularParamsMethodBinding> granularParamsMethodBindings =
                    methodBindings
                            .stream()
                            .filter(mb -> mb instanceof GranularParamsMethodBinding)
                            .map(mb -> (GranularParamsMethodBinding) mb)
                            .collect(Collectors.toList());

            return granularParamsMethodDispatcherFactory.create(granularParamsMethodBindings, methodName);
        }
    }
}
