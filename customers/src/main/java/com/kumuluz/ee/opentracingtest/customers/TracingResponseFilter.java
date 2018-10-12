package com.kumuluz.ee.opentracingtest.customers;


import com.kumuluz.ee.opentracingtest.tracing.Tracing;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class TracingResponseFilter implements ContainerResponseFilter {
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Tracing.stopServiceSpan(requestContext);
    }
}
