package com.pushpopsoft.jsonrpclib.server.handlers.dispatch.resolvers;

import com.pushpopsoft.jsonrpclib.server.exceptions.InvalidParamsException;
import com.pushpopsoft.jsonrpclib.server.handlers.methods.NamedParamsMethod;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class NamedParamsMethodResolver {
    private final List<NamedParamsMethod> namedParamsMethods;

    public NamedParamsMethodResolver(List<NamedParamsMethod> namedParamsMethods) {
        this.namedParamsMethods = namedParamsMethods;
    }

    public NamedParamsMethod resolve(Set<String> callerParameters) throws InvalidParamsException {
        Objects.requireNonNull(callerParameters, "'callerParameters' cannot be null");

        List<NamedParamsMethod> matches = findMatches(callerParameters);

        if (matches.isEmpty()) {
            String message = "Supplied parameters doesn't match. " + parametersDescription(namedParamsMethods);
            throw new InvalidParamsException(message);
        }
        else if (matches.size() == 1) {
            return matches.get(0);
        }
        else {
            String message = "It is ambiguous which method overload to choose. Matched overloads: " + matches;
            throw new InvalidParamsException(message);
        }
    }

    private List<NamedParamsMethod> findMatches(Set<String> callerParameters) {

        return namedParamsMethods
                .stream()
                .filter(candidate -> isMatching(candidate, callerParameters))
                .collect(Collectors.toList());
    }

    private static boolean isMatching(NamedParamsMethod candidate, Set<String> callerParameters) {
        return candidate.getNamedParamDefinitions()
                .stream()
                .filter(namedParam -> namedParam.mandatory)
                .allMatch(namedParam -> callerParameters.contains(namedParam.name));
    }

    private static String parametersDescription(List<NamedParamsMethod> namedParamsMethods) {
        StringBuilder sb = new StringBuilder(namedParamsMethods.size());
        for (NamedParamsMethod namedParamsMethod : namedParamsMethods) {
            sb.append(parameterDescription(namedParamsMethod));
        }
        return sb.toString();
    }

    private static String parameterDescription(NamedParamsMethod md) {
        return String.format("method: %s", md.toString());
    }
}
