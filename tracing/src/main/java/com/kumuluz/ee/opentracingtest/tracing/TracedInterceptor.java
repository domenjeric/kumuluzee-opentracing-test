package com.kumuluz.ee.opentracingtest.tracing;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@Traced
@Interceptor
public class TracedInterceptor {

    @AroundInvoke
    public Object trace(InvocationContext context) throws Exception {

        Tracer tracer = GlobalTracer.get();

        try {
            Span parentSpan = tracer.activeSpan();
            Method method = context.getMethod();
            Span span = tracer.buildSpan(method.getName())
                    .asChildOf(parentSpan)
                    .withTag("app.className", context.getTarget().getClass().getSuperclass().getName())
                    .withTag("app.methodName", method.getName())
                    .startActive(true)
                    .span();

            Object toReturn = context.proceed();

            span.finish();

            return toReturn;
        } catch(Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
