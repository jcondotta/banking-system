# Observability Guidelines

Use this guide when changing structured logs, metrics, observations, tracing, correlation, or telemetry configuration. Application owns business outcomes and failure classification as described in [Application](07-application-guidelines.md#logging-failures-and-observability); adapters own protocol context and runtime wiring. Use [Design](02-design.md) for changes to observable behavior or data exposure, and local guidance for service-specific conventions.

## Find The Relevant Instrumentation

| Change | Inspect together |
| --- | --- |
| Operation logs | Handler, `*Operation`, `*LogKey`, `*FailureReason`, and nearest logging test |
| Correlation | Entry filter/interceptor, context provider, outgoing metadata, and asynchronous boundaries |
| Metrics or tracing | Instrumented method, annotations/aspects, registry/export dependencies, and active configuration |
| Log output or export | `logback-spring.xml`, application profiles, encoder/appender configuration, and destination |

Inspect only the affected path. Do not assume every service or transport has the same instrumentation.

## Structured Logs

Handlers using scoped logging identify operations with `@LogScope` and add structured fields through `LogContext`. Reuse the context's operation names, field keys, and normalized failure reasons rather than introducing equivalent strings.

An existing example is [CreateRecipientCommandHandler](../../services/recipients/src/main/java/com/jcondotta/banking/recipients/application/recipient/command/create/CreateRecipientCommandHandler.java): it records success at INFO, domain failures at WARN, and unexpected failures at ERROR with the exception. It adds account/recipient identifiers and masks IBAN on failure. Follow the relevant local handler's semantics rather than applying these levels indiscriminately.

Keep operation names, outcomes, and reason values stable unless the agreed change requires otherwise. Check existing scopes and emissions before adding another log for the same outcome. Preserve exception propagation and business behavior when instrumenting a failure.

Do not emit secrets or sensitive payloads through structured fields, formatted messages, or exception details. Keep authorized response data separate from diagnostic data; an IBAN allowed in an API response is not thereby allowed unmasked in logs.

## Correlation And Context Lifetime

Trace the context through the actual entry, application call, and outgoing message or client. Existing mechanisms include:

- Shared `RequestLoggingContextFilter`: resolves a correlation ID, returns it in the response header, binds `ScopedCorrelationIdProvider.CORRELATION_ID`, and manages MDC fields for the request. It skips actuator paths and removes its MDC fields in `finally`.
- `ScopedCorrelationIdProvider`: returns the bound ID or generates one when no scope is bound. Reusing an existing request ID therefore depends on the calling scope.
- Ledger's `CorrelationMdcKafkaInterceptor`: reads correlation from the message envelope into MDC and removes it in success/failure callbacks. This does not itself bind the HTTP provider's scoped value.

When introducing asynchronous execution or changing consumers, inspect context capture, propagation, and cleanup explicitly. Check consecutive operations and failure paths for context leakage. Do not assume that a value in MDC or an event envelope automatically crosses threads, processes, or protocols.

## Metrics, Observations, And Tracing

Use `@Observed` or other instrumentation only where there is an established requirement and configuration. Preserve observation names and low-cardinality dimensions; keep identifiers, IBANs, free-form input, and exception messages out of metric tags.

Before adding timers or observations, inspect existing framework instrumentation and aspects. The shared `MetricsConfig` supplies `TimedAspect`; some handlers use `@Observed`. Determine whether measurements describe distinct boundaries or would count the same operation twice.

Correlation IDs support log lookup; they do not prove distributed trace continuity. Check the affected service's tracing dependencies, propagation, sampling, and exporter configuration before claiming spans are connected or exported. An annotation or dependency alone does not establish runtime behavior.

## Configuration And Verification

Check active profiles, annotation enablement, meter filters, histogram/percentile settings, endpoint exposure, and exporters relevant to the change. Accounts and recipients configure OTLP metrics export with an environment-controlled default of disabled; inspect the deployed profile before expecting data at a backend.

For log output changes, inspect encoders as well as log calls. Recipients' `logback-spring.xml` includes structured fields and MDC, with different local and non-local appenders. Verify the actual output path rather than assuming console output proves remote ingestion.

Use [Structured Logging Tests](04-testing.md#structured-logging-tests) for capture and assertions. `CreateRecipientCommandHandlerTest`, `ScopedCorrelationIdProviderTest`, and `CorrelationMdcKafkaInterceptorTest` are existing entry points. Check proxy/aspect setup when testing annotation-driven behavior. Follow [Testing](04-testing.md) for integration scope; distinguish local assertions from verified backend delivery in [Validation](05-validation.md).
