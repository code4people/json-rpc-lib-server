package com.code4people.jsonrpclib.server;

import java.util.Optional;

public interface ServiceActivator {
    Optional<String> processMessage(String message);
}
