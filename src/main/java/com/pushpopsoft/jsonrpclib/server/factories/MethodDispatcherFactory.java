package com.pushpopsoft.jsonrpclib.server.factories;

import com.pushpopsoft.jsonrpclib.binding.BindingErrorException;
import com.pushpopsoft.jsonrpclib.binding.MethodBinding;
import com.pushpopsoft.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.pushpopsoft.jsonrpclib.binding.info.MethodInfo;
import com.pushpopsoft.jsonrpclib.binding.info.SingleArgumentMethodInfo;
import com.pushpopsoft.jsonrpclib.server.handlers.dispatch.MethodDispatcher;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodDispatcherFactory {

    private final MethodOverloadRouterFactory methodOverloadRouterFactory;
    private final SingleArgumentMethodDispatcherFactory createSingleArgumentMethodDispatcherFactory;

    public MethodDispatcherFactory(MethodOverloadRouterFactory methodOverloadRouterFactory, SingleArgumentMethodDispatcherFactory createSingleArgumentMethodDispatcherFactory) {
        this.methodOverloadRouterFactory = methodOverloadRouterFactory;
        this.createSingleArgumentMethodDispatcherFactory = createSingleArgumentMethodDispatcherFactory;
    }

    public MethodDispatcher create(String methodName, List<MethodBinding<? extends MethodInfo>> methodInfos) {
        Optional<MethodBinding<SingleArgumentMethodInfo>> singleArgumentMethodBinding =
                methodInfos
                        .stream()
                        .filter(mb -> mb.getMethodInfo() instanceof SingleArgumentMethodInfo)
                        .map(mb -> (MethodBinding<SingleArgumentMethodInfo>) mb)
                        .findFirst();

        if (singleArgumentMethodBinding.isPresent()) {
            if (methodInfos.size() == 1) {
                return createSingleArgumentMethodDispatcherFactory.create(singleArgumentMethodBinding.get());
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
            List<MethodBinding<GranularParamsMethodInfo>> granularParamsMethodBindings =
                    methodInfos
                            .stream()
                            .filter(mb -> mb.getMethodInfo() instanceof GranularParamsMethodInfo)
                            .map(mb -> (MethodBinding<GranularParamsMethodInfo>) mb)
                            .collect(Collectors.toList());

            return methodOverloadRouterFactory.create(granularParamsMethodBindings, methodName);
        }
    }
}
