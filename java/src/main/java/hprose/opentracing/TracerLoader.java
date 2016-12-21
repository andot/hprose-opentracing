package hprose.opentracing;

import io.opentracing.NoopTracerFactory;
import io.opentracing.Tracer;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TracerLoader {
    private static final Logger LOGGER = Logger.getLogger(TracerLoader.class.getName());
    public static Tracer loadTracer() {
        try {
            Class loaderClass = Class.forName("java.util.ServiceLoader");
            Method load = loaderClass.getMethod("load", Class.class);
            Method iterator = loaderClass.getMethod("iterator");
            Iterator<Tracer> tracers = (Iterator<Tracer>)iterator.invoke(
                load.invoke(null, Tracer.class)
            );
            if (tracers.hasNext()) {
                Tracer tracer = tracers.next();
                if (!tracers.hasNext()) {
                    return tracer;
                }
                LOGGER.log(Level.WARNING,
                        "More than one Tracer service implementation found. " +
                        "Falling back to NoopTracer implementation.");
            }
        }
        catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getLocalizedMessage());
        }
        return NoopTracerFactory.create();
    }
}
