package org.murlan.live.endpoint.req.handler;

import org.murlan.live.endpoint.req.ReqContext;
import org.murlan.live.endpoint.req.ReqHandlerResult;
import org.murlan.live.protocol.api.Req;

import java.io.IOException;

public interface ReqHandler<R extends Req> {
    Class<R> requestType();
    ReqHandlerResult handle(R req, ReqContext context) throws IOException, InterruptedException;
}
