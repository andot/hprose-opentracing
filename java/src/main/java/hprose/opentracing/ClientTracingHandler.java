package hprose.opentracing;

import hprose.client.ClientContext;
import hprose.common.HproseContext;
import hprose.common.InvokeHandler;
import hprose.common.InvokeSettings;
import hprose.common.NextInvokeHandler;
import hprose.util.concurrent.Action;
import hprose.util.concurrent.Promise;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClientTracingHandler implements InvokeHandler {
    public static final String KEY_NAME = "io.opentracing.active-span";
    private final Tracer tracer;

    public ClientTracingHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    private Span createSpanFromParent(Object parentSpan, String operationName) {
        if ((parentSpan != null) && (parentSpan instanceof Span)) {
            return tracer.buildSpan(operationName).asChildOf((Span)parentSpan).start();
        }
        else {
            return tracer.buildSpan(operationName).start();
        }
    }

    private Map<String, List<String>> getHttpHeader(HproseContext context) {
        Map<String, List<String>> header = (Map<String, List<String>>)(context.get("httpHeader"));
        if (header == null) {
            return new HashMap<String, List<String>>();
        }
        return header;
    }

    public Promise<Object> handle(String name, Object[] args, HproseContext context, NextInvokeHandler next) {
        final Span span = createSpanFromParent(context.get(KEY_NAME), name);
        final ClientContext clientContext = (ClientContext)context;
        final InvokeSettings settings = clientContext.getSettings();
        span.setTag("hprose.method_name", name);
        span.setTag("hprose.return_type", settings.getReturnType().toString());
        span.setTag("hprose.mode", settings.getMode().toString());
        span.setTag("hprose.byref", settings.isByref());
        span.setTag("hprose.async", settings.isAsync());
        span.setTag("hprose.oneway", settings.isOneway());
        span.setTag("hprose.simple", settings.isSimple());
        span.setTag("hprose.idempotent", settings.isIdempotent());
        span.setTag("hprose.failswitch", settings.isFailswitch());
        span.setTag("hprose.retry", settings.getRetry());
        span.setTag("hprose.timeout", settings.getTimeout());
        final Map<String, List<String>> httpHeader = getHttpHeader(context);
        tracer.inject(span.context(), Format.Builtin.HTTP_HEADERS, new TextMap() {
            @Override
            public Iterator<Map.Entry<String, String>> iterator() {
                throw new UnsupportedOperationException(
                "TextMapInjectAdapter should only be used with Tracer.inject()");
            }
            @Override
            public void put(String key, String value) {
                httpHeader.put(key, Arrays.asList(value));
            }
        });
        context.set("httpHeader", httpHeader);
        return next.handle(name, args, context).whenComplete(new Action<Object>() {
            public void call(Object value) throws Throwable {
                if (value instanceof Throwable) {
                    Throwable error = ((Throwable)value);
                    if (error.getCause() == null) {
                        span.log(error.getMessage(), error.getCause().getMessage());
                    }
                    else {
                        span.log("Error", error.getMessage());
                    }
                }
                span.finish();
            }
        });
    }

}
