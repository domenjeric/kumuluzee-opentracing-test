package com.kumuluz.ee.opentracingtest.orders;

import com.kumuluz.ee.opentracingtest.tracing.Tracing;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class TracingRequestFilter implements ContainerRequestFilter {

    public void filter(ContainerRequestContext requestContext) throws IOException {
        Tracing.startServiceSpan(requestContext, "OrderService");
    }

}
