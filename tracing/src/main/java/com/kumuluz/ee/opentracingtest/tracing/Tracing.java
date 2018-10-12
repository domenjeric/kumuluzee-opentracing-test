package com.kumuluz.ee.opentracingtest.tracing;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.util.GlobalTracer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;

public class Tracing {

    public static void init(String serviceName) {
        Configuration.SamplerConfiguration samplerConfig = new Configuration.SamplerConfiguration()
                .withType(ConstSampler.TYPE)
                .withParam(1);
        Configuration.SenderConfiguration senderConfig = new Configuration.SenderConfiguration()
                .withAgentHost("localhost")//TODO: read from config
                .withAgentPort(5775);
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
                spanBuilder.asChildOf(parentSpan);
            }

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
