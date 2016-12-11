package hprose.opentracing;

import hprose.common.HproseContext;
import hprose.common.NextInvokeHandler;
import static hprose.opentracing.HttpClientTracingInvokeHandler.KEY_NAME;
import hprose.server.HproseHttpService;
import hprose.server.HttpContext;
import hprose.util.concurrent.Promise;
import io.opentracing.Tracer;

public class HttpClientTracingExInvokeHandler extends HttpClientTracingInvokeHandler {
    public HttpClientTracingExInvokeHandler(Tracer tracer) {
        super(tracer);
    }
    @Override
    public Promise<Object> handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) {
        HttpContext httpContext = HproseHttpService.getCurrentContext();
        if (httpContext != null) {
            context.set(KEY_NAME, httpContext.get(KEY_NAME));
        }
        return super.handle(name, args, context, next);
    }
}
