package com.pushpopsoft.jsonrpclib.server.handlers.methods;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.pushpopsoft.jsonrpclib.server.exceptions.BaseErrorException;
import com.pushpopsoft.jsonrpclib.server.exceptions.InvalidParamsException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NamedParamsMethod {
    private final JsonMethodAdapter jsonMethodAdapter;
    private final Set<String> mandatoryParams;
    private final List<NamedParamDefinition> namedParamDefinitions;

    public NamedParamsMethod(List<NamedParamDefinition> namedParamDefinitions,
                             JsonMethodAdapter jsonMethodAdapter) {
        this.jsonMethodAdapter = jsonMethodAdapter;
        this.namedParamDefinitions = namedParamDefinitions;
        this.mandatoryParams = namedParamDefinitions
                .stream()
                .filter(namedParam -> namedParam.mandatory)
                .map(namedParam -> namedParam.name)
                .collect(Collectors.toSet());
    }

    public JsonNode invoke(Map<String, JsonNode> jsonParameters) throws BaseErrorException {
        if (!jsonParameters.keySet().containsAll(mandatoryParams)) {
            String message = String.format("Missing parameters. Provided: '%s', Mandatory: %s", jsonParameters.keySet(), mandatoryParams);
            throw new InvalidParamsException(message);
        }
        JsonNode[] params = new JsonNode[namedParamDefinitions.size()];
        int index = 0;
        for (NamedParamDefinition namedParamDefinition : namedParamDefinitions) {
            params[index++] = jsonParameters.containsKey(namedParamDefinition.name)
                    ? jsonParameters.get(namedParamDefinition.name)
                    : MissingNode.getInstance();
        }

        return jsonMethodAdapter.invoke(params);
    }

    public List<NamedParamDefinition> getNamedParamDefinitions() {
        return namedParamDefinitions;
    }

    @Override
    public String toString() {
        return String.format("Parameters: %s", namedParamDefinitions);
    }
}
