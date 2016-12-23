<p align="center"><img src="http://hprose.com/banner.@2x.png" alt="Hprose" title="Hprose" width="650" height="200" /></p>

# Hprose-Java OpenTracing

## 安装

该包你可以直接下载源码使用，也可以通过 maven 或 gradle 来添加到你的项目中，例如：

**Maven**

```xml
<dependencies>
    <dependency>
        <groupId>io.opentracing.contrib.hprose</groupId>
        <artifactId>opentracing-hprose-java</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

**Gradle**

```
compile 'io.opentracing.contrib.hprose:opentracing-hprose-java:0.1.0'
```

## 快速入门

该版本目前仅支持对 hprose 在 http 和 https 协议下的追踪功能。

### 服务器

#### 配置方式

如果你使用的是默认 hprose servlet 配置方式来发布服务。

那么你只需要在 servlet 的配置文件中加入：

```xml
<init-param>
    <param-name>invoke</param-name>
    <param-value>io.opentracing.contrib.hprose.HttpServiceTracingInvokeHandler</param-value>
</init-param>
```

即可。

如果你除了 `HttpServiceTracingInvokeHandler` 还有多个 `InvokeHandler` 需要配置，只需要用逗号（`,`）分隔即可。

在该方式下， `Tracer` 实现会自动通过 spi 方式载入，所以请确认 spi 的配置文件中已加入了 `Tracer` 的实现类，另外需要注意的是，`Tracer` 实现类在配置文件中必须是唯一的，不能同时配置多个实现类。

#### 编码方式

如果你使用编码方式来发布服务，例如自定义 Servlet：

```java
package hprose.exam.server;
import hprose.common.HproseMethods;
import hprose.server.HproseServlet;
import io.opentracing.contrib.hprose.HttpServiceTracingInvokeHandler;

public class MyHproseServlet extends HproseServlet {
    public String hello(String name) {
        return "Hello " + name;
    }
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        service.use(new HttpServiceTracingInvokeHandler());
        service.add("hello", this);
    }
}
```

通过该方式加载 `HttpServiceTracingInvokeHandler` 时，你除了可以通过无参构造函数创建 `HttpServiceTracingInvokeHandler` 对象以外，你可以通过有参构造函数将具体的 `Tracer` 实现实例作为参数传入。

例如：

```java
service.use(new HttpServiceTracingInvokeHandler(tracer));
```

### 客户端

如果你是通过 maven 方式安装的 hprose，或者是从 [github](https://github.com/hprose/hprose-java/tree/master/dist) 下载的完整版的 jar 包。那么你可以在创建客户端之后，使用：

```java
client.use(new HttpClientTracingExInvokeHandler(tracer));
```

方式来开启 Opentracing 功能。

如果你使用的是从 [github](https://github.com/hprose/hprose-java/tree/master/dist) 下载的纯客户端版的 jar 包。那么你可以在创建客户端之后，使用：

```java
client.use(new HttpClientTracingInvokeHandler(tracer));
```

来开启 Opentracing 功能。

这两种方式在创建 Invokehandler 对象时，都可以使用无参构造函数，使用无参构造函数时，`Tracer` 的具体实现从 spi 配置中自动加载。