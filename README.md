<p align="center"><img src="http://hprose.com/banner.@2x.png" alt="Hprose" title="Hprose" width="650" height="200" /></p>

# Hprose-OpenTracing

This package enables distributed tracing in hprose clients and servers via [The OpenTracing Project](http://opentracing.io/): a set of consistent, expressive, vendor-neutral APIs for distributed tracing and context propagation.

Once a production system contends with real concurrency or splits into many services, crucial (and formerly easy) tasks become difficult: user-facing latency optimization, root-cause analysis of backend errors, communication about distinct pieces of a now-distributed system, etc. Distributed tracing follows a request on its journey from inception to completion from mobile/browser all the way to the microservices.

As core services and libraries adopt OpenTracing, the application builder is no longer burdened with the task of adding basic tracing instrumentation to their own code. In this way, developers can build their applications with the tools they prefer and benefit from built-in tracing instrumentation. OpenTracing implementations exist for major distributed tracing systems and can be bound or swapped with a one-line configuration change.

## Further Information

If you’re interested in learning more about the OpenTracing standard, join the conversation on our [mailing list](http://opentracing.us13.list-manage.com/subscribe?u=180afe03860541dae59e84153&id=19117aa6cd) or [Gitter](https://gitter.im/opentracing/public).

If you want to learn more about the underlying API for your platform, visit the [source code](https://github.com/opentracing/).

If you would like to implement OpenTracing in your project and need help, feel free to send us a note at [community@opentracing.io](mailto:community@opentracing.io).