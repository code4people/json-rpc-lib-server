package com.code4people.jsonrpclib.server.handlers.methods;

public class NamedParamDefinition {
    public final String name;
    public final boolean mandatory;

    public NamedParamDefinition(String name, boolean mandatory) {
        this.name = name;
        this.mandatory = mandatory;
    }
}
