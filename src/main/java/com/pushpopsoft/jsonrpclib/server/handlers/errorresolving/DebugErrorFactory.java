package com.pushpopsoft.jsonrpclib.server.handlers.errorresolving;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.pushpopsoft.jsonrpclib.server.serialization.SerializationException;
import com.pushpopsoft.jsonrpclib.server.serialization.SerializationService;

public class DebugErrorFactory {

    private final DebugErrorDetailLevel debugErrorDetailLevel;
    private final SerializationService serializationService;

    public DebugErrorFactory(DebugErrorDetailLevel debugErrorDetailLevel, SerializationService serializationService) {
        this.debugErrorDetailLevel = debugErrorDetailLevel;
        this.serializationService = serializationService;
    }

    public JsonNode create(String detailMessage, Throwable cause) {
        Object debugErrorObject;
        if (debugErrorDetailLevel == DebugErrorDetailLevel.DETAIL_MESSAGE) {
            debugErrorObject = new DebugErrorMessage(detailMessage);
        }
        else if (debugErrorDetailLevel == DebugErrorDetailLevel.STACKTRACE) {
            debugErrorObject = new DebugErrorData(cause, detailMessage);
        }
        else if (debugErrorDetailLevel == DebugErrorDetailLevel.NO_DETAIL){
            return null;
        }
        else {
            throw new Error("Unknown DebugErrorDetailLevel value: " + debugErrorDetailLevel);
        }
        try {
            return serializationService.serialize(debugErrorObject);
        } catch (SerializationException e) {
            return TextNode.valueOf("Cannot provide more detail for this error, because debug error data failed to deserialize. Reason: " + e.getMessage());
        }
    }

    public static class DebugErrorData {
        public final Throwable exception;
        public final String detailMessage;

        public DebugErrorData(Throwable exception, String detailMessage) {
            this.exception = exception;
            this.detailMessage = detailMessage;
        }
    }

    public static class DebugErrorMessage {
        public final String detailMessage;
        private DebugErrorMessage(String detailMessage) {
            this.detailMessage = detailMessage;
        }
    }
}
