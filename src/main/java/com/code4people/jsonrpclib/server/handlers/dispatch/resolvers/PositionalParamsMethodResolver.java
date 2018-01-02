package com.code4people.jsonrpclib.server.handlers.dispatch.resolvers;

import com.code4people.jsonrpclib.server.exceptions.InvalidParamsException;
import com.code4people.jsonrpclib.server.handlers.methods.PositionalParamsMethod;

import java.util.List;
import java.util.stream.Collectors;

public class PositionalParamsMethodResolver {

    private final List<PositionalParamsMethod> positionalParamsMethods;

    public PositionalParamsMethodResolver(List<PositionalParamsMethod> positionalParamsMethods) {
        this.positionalParamsMethods = positionalParamsMethods;
    }

    public PositionalParamsMethod resolve(int numberOfParams) throws InvalidParamsException {

        List<PositionalParamsMethod> matches = positionalParamsMethods
                .stream()
                .filter(m -> isMatching(m, numberOfParams))
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            String message = String.format(
                    "Wrong number of parameters. Number of sent parameters is '%s'.",
                    numberOfParams,
                    parametersDescription(positionalParamsMethods));
            throw new InvalidParamsException(message);
        }
        else if (matches.size() == 1) {
            return matches.get(0);
        }
        else {
            int minDiffInParamsCount = matches
                    .stream()
                    .mapToInt(m -> m.getParamsCount() - numberOfParams)
                    .min()
                    .getAsInt();

            List<PositionalParamsMethod> ambiguousMatches = matches
                    .stream()
                    .filter(m -> (m.getParamsCount() - numberOfParams) == minDiffInParamsCount)
                    .collect(Collectors.toList());

            if (ambiguousMatches.size() == 1) {
                return ambiguousMatches.get(0);
            }

            String message = "It is ambiguous which method to choose. Ambiguous methods: " + ambiguousMatches;
            throw new InvalidParamsException(message);
        }
    }

    private static boolean isMatching(PositionalParamsMethod method, int numberOfParams) {
        return method.getMandatoryParamsCount() <= numberOfParams && method.getParamsCount() >= numberOfParams;
    }

    private static String parametersDescription(List<PositionalParamsMethod> methods) {
        StringBuilder sb = new StringBuilder(methods.size());
        for (PositionalParamsMethod method : methods) {
            sb.append(String.format("method: %s", method));
        }
        return sb.toString();
    }
}
