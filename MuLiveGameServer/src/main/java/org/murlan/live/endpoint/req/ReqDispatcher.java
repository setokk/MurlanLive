package org.murlan.live.endpoint.req;

import org.murlan.live.endpoint.req.handler.ReqHandler;
import org.murlan.live.protocol.api.Req;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReqDispatcher {
    private final Map<Class<? extends Req>, ReqHandler<? extends Req>> handlers;

    public ReqDispatcher(List<ReqHandler<? extends Req>> handlers) {
        this.handlers = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(ReqHandler::requestType, Function.identity()));
    }

    @SuppressWarnings("unchecked")
    public ReqHandlerResult dispatch(Req req, ReqContext context) throws IOException, InterruptedException {
        ReqHandler<Req> handler = (ReqHandler<Req>) handlers.get(req.getClass());
        if (handler == null) {
            throw new IllegalStateException("No handler for request: " + req);
        }
        return handler.handle(req, context);
    }
}
