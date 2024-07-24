# Akka OpenTelemetry Instrumentation Example

## Problem

The Akka instrumentation is adding extraneous `/` to `http.route` attribute which also has an effect on the span name. This
repository has an example where this behavior is readily reproducible.

## Assumptions and Requirements

- The OpenTelemetry Java Instrumentation agent is downloaded locally
- Java
- Maven

## Running the Example

- Run `mvn package`
- Set `export OTEL_TRACES_EXPORTER=logging`
- Run `java -javaagent:./opentelemetry-javaagent.jar -jar target/akka-example-1.0-SNAPSHOT-shaded.jar`
- Access endpoints such `http://localhost:8080/ball/2`, `http://localhost:8080/ball/` and observe generated spans

## Observations

- Accessing the above mentioned URLs results in the following spans.

```
[otel.javaagent 2024-07-24 14:57:05:121 -0400] [routes-akka.actor.default-dispatcher-8] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET //ball//*' : 35fe9b0512e1217738533654803c9bf4 6b6b12825fb4473d SERVER [tracer: io.opentelemetry.akka-http-10.0:2.6.0-alpha] AttributesMap{data={http.route=//ball//*, network.protocol.version=1.1, http.request.method=GET, url.path=/ball/2, server.address=localhost, user_agent.original=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36, http.response.status_code=200, url.scheme=http, thread.id=43, thread.name=routes-akka.actor.default-dispatcher-8, server.port=8080}, capacity=128, totalAddedValues=11}
[otel.javaagent 2024-07-24 14:57:24:499 -0400] [routes-akka.actor.default-dispatcher-5] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET //ball/' : 49bd28ea4fbbd6eb12b672282958790d fdddc44f379a091a SERVER [tracer: io.opentelemetry.akka-http-10.0:2.6.0-alpha] AttributesMap{data={http.route=//ball/, network.protocol.version=1.1, http.request.method=GET, url.path=/ball/, server.address=localhost, user_agent.original=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36, http.response.status_code=200, url.scheme=http, thread.id=40, thread.name=routes-akka.actor.default-dispatcher-5, server.port=8080}, capacity=128, totalAddedValues=11}
```

In both cases, the `url.path` attribute appears to have the correction route as opposed to the `http.route` attribute or 
the span name, both of which have extraneous `/` in them.