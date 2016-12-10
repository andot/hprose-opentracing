<p align="center"><img src="http://hprose.com/banner.@2x.png" alt="Hprose" title="Hprose" width="650" height="200" /></p>

# Hprose-Java OpenTracing

## Installation

This package is available on Maven Central and can be added to your project as follows:

**Maven**

```xml
<dependencies>
    <dependency>
        <groupId>org.hprose.opentracing</groupId>
        <artifactId>hprose-opentracing</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

**Gradle**

```
compile 'org.hprose.opentracing:hprose-opentracing:0.1.0'
```

## Quickstart

If you want to add basic tracing to your hprose http client and http server, you can do so in a few short and simple steps, as shown below.

**HTTP Server**

```java
service.use(new HttpServiceTracingInvokeHandler(tracer));
```

**HTTP Client**

***standalone client***

```java
client.use(new HttpClientTracingInvokeHandler(tracer));
```

***client in server***

```java
client.use(new HttpClientTracingExInvokeHandler(tracer));
```

