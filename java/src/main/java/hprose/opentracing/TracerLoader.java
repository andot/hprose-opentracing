package hprose.opentracing;

import io.opentracing.Tracer;
import java.lang.reflect.Method;
import java.util.Iterator;

public class TracerLoader {
    public static Tracer loadTracer() {
        try {
            Class loaderClass = Class.forName("java.util.ServiceLoader");
            Method load = loaderClass.getMethod("load", Class.class);
            Method iterator = loaderClass.getMethod("iterator");
            Iterator<Tracer> tracers = (Iterator<Tracer>)iterator.invoke(
                load.invoke(null, Tracer.class)
            );
            if (tracers.hasNext()) return tracers.next();
        }
        catch (Exception ex) {}
        return null;
    }
}
