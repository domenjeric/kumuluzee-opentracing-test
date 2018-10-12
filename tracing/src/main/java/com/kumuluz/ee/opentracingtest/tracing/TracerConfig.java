package com.kumuluz.ee.opentracingtest.tracing;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("tracer-config")
public class TracerConfig {
    @ConfigValue("selected-tracer")
    private String selectedTracer;

    @ConfigValue("available-tracers.jaeger.reporter_host")
    private String jaegerReporterHost;

    @ConfigValue("available-tracers.jaeger.reporter_port")
    private int jaegerReporterPort;

    @ConfigValue("available-tracers.zipkin.reporter_host")
    private String zipkinReporterHost;

    @ConfigValue("available-tracers.zipkin.reporter_port")
    private int zipkinReporterPort;


    public String getSelectedTracer() {
        return selectedTracer;
    }

    public String getJaegerReporterHost() {
        return jaegerReporterHost;
    }

    public int getJaegerReporterPort() {
        return jaegerReporterPort;
    }

    public String getZipkinReporterHost() {
        return zipkinReporterHost;
    }

    public int getZipkinReporterPort() {
        return zipkinReporterPort;
    }
}
