package com.kumuluz.ee.opentracingtest.orders;

import com.kumuluz.ee.opentracingtest.tracing.Tracing;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class OrderApplication extends Application {

    public OrderApplication() {
        Tracing.init("OrderService");
    }
}
