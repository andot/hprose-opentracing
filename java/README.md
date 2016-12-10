<p align="center"><img src="http://hprose.com/banner.@2x.png" alt="Hprose" title="Hprose" width="650" height="200" /></p>

# Hprose-Java OpenTracing

## Installation

This package is available on Maven Central and can be added to your project as follows:

**Maven**

```xml
<dependencies>
    <dependency>
        <groupId>org.hprose</groupId>
        <artifactId>hprose-opentracing</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

**Gradle**

```
compile 'org.hprose:hprose-opentracing:2.0.0'
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

