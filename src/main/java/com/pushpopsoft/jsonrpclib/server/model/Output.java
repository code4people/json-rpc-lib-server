package com.pushpopsoft.jsonrpclib.server.model;

public class Output {
    private final Response[] batch;
    private final Response single;

    public Output(Response[] batch) {
        this.batch = batch;
        this.single = null;
    }

    public Output(Response single) {
        this.batch = null;
        this.single = single;
    }

    public boolean isBatch() {
        return batch != null;
    }

    public Response[] getBatch() {
        return batch;
    }

    public Response getSingle() {
        return single;
    }
}
