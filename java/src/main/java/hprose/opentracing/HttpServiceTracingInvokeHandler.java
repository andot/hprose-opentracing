package hprose.opentracing;

import hprose.common.HproseContext;
import hprose.common.InvokeHandler;
import hprose.common.NextInvokeHandler;
import hprose.server.HttpContext;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class HttpServiceTracingInvokeHandler implements InvokeHandler {
    private final Tracer tracer;

    public HttpServiceTracingInvokeHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    private Span getSpanFromHeaders(Map<String, String> headers, String operationName) {
        Span span;
        try {
            SpanContext parentSpanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS,
                new TextMapExtractAdapter(headers));
            if (parentSpanCtx == null) {
                span = tracer.buildSpan(operationName).start();
            } else {
                span = tracer.buildSpan(operationName).asChildOf(parentSpanCtx).start();
            }
        } catch (IllegalArgumentException iae){
            span = tracer.buildSpan(operationName)
                .withTag("Error", "Extract failed and an IllegalArgumentException was thrown")
                .start();
        }
        return span;
    }

    private Map<String, String> getHttpHeader(HttpContext httpContext) {
        Map<String, String> header = new HashMap<String, String>();
        HttpServletRequest request = httpContext.getRequest();
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            header.put(name, request.getHeader(name));
        }
        return header;
    }

    public Promise<Object> handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) {
        final HttpContext httpContext = (HttpContext)context;
        final Span span = getSpanFromHeaders(getHttpHeader(httpContext), name);
        span.setTag("hprose.method_name", name);
        span.setTag("hprose.method_alias", httpContext.getRemoteMethod().aliasName);
        span.setTag("hprose.method_type", httpContext.getRemoteMethod().method.toGenericString());
        span.setTag("hprose.mode", httpContext.getRemoteMethod().mode.toString());
        span.setTag("hprose.byref", httpContext.isByref());
        span.setTag("hprose.oneway", httpContext.getRemoteMethod().oneway);
        span.setTag("hprose.simple", httpContext.getRemoteMethod().simple);
        context.set(HttpClientTracingInvokeHandler.KEY_NAME, span);
        return next.handle(name, args, context).whenComplete(new Action<Object>() {
            public void call(Object value) throws Throwable {
                if (value instanceof Throwable) {
                    Throwable error = ((Throwable)value);
                    if (error.getCause() == null) {
                        span.log(error.getCause().getMessage());
                    }
                    else {
                        span.log(error.getMessage());
                    }
                }
                else {
                    span.log("Call completed");
                }
                span.finish();
            }
        });
    }

}
