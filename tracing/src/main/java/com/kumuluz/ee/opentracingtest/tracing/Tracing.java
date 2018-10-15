package com.kumuluz.ee.opentracingtest.tracing;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.Optional;

public class Tracing {

    public static void init(String serviceName) {
        Optional<String> jaegerHost = ConfigurationUtil.getInstance()
                .get("tracer-config.available-tracers.jaeger.reporter_host");
        Optional<Integer> jaegerPort = ConfigurationUtil.getInstance()
                .getInteger("tracer-config.available-tracers.jaeger.reporter_port");


        Configuration.SamplerConfiguration samplerConfig = new Configuration.SamplerConfiguration()
                .withType(ConstSampler.TYPE)
                .withParam(1);
        Configuration.SenderConfiguration senderConfig = new Configuration.SenderConfiguration()
                .withAgentHost(Optional.of(jaegerHost).get().orElse("localhost"))
                .withAgentPort(Optional.of(jaegerPort).get().orElse(5775));
        Configuration.ReporterConfiguration reporterConfig = new Configuration.ReporterConfiguration()
                .withLogSpans(true)
                .withFlushInterval(1000)
                .withMaxQueueSize(10000)
                .withSender(senderConfig);

        Tracer tracer = new Configuration(serviceName).withSampler(samplerConfig).withReporter(reporterConfig).getTracer();
        GlobalTracer.register(tracer);
    }

    public static void startServiceSpan(ContainerRequestContext requestContext, String operationName) {
        Tracer tracer = GlobalTracer.get();
        MultivaluedMap<String, String> rawHeaders = requestContext.getHeaders();
        HashMap<String, String> headers = extractHeaders(rawHeaders);
        Tracer.SpanBuilder spanBuilder;

        try {
            SpanContext parentSpan = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));
            spanBuilder = tracer.buildSpan(operationName);

            if (parentSpan != null) {
                spanBuilder = spanBuilder.asChildOf(parentSpan);
            }

            spanBuilder = spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)
                            .withTag(Tags.HTTP_METHOD.getKey(), requestContext.getMethod())
                            .withTag(Tags.HTTP_URL.getKey(),
                                    requestContext.getUriInfo().getBaseUri().toString() + requestContext.getUriInfo().getPath());

            requestContext.setProperty("opentracing-span", spanBuilder.startActive(true).span());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void stopServiceSpan(ContainerRequestContext requestContext) {
        try{
            Span span = (Span) requestContext.getProperty("opentracing-span");
            span.finish();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    private static HashMap<String, String> extractHeaders(MultivaluedMap<String, String> rawHeaders) {
        HashMap<String, String> headers = new HashMap<>();

        for (String key : rawHeaders.keySet()) {
            headers.put(key, rawHeaders.get(key).get(0));
        }

        return headers;
    }
}
