package com.kumuluz.ee.opentracingtest.customers;

import com.kumuluz.ee.opentracingtest.tracing.Tracing;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class CustomerApplication extends Application {

    public CustomerApplication() {
        Tracing.init("CustomerService");
    }
}
