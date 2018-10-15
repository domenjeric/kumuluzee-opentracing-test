package com.kumuluz.ee.opentracingtest.customers;


import com.kumuluz.ee.opentracingtest.tracing.Traced;

import java.io.Serializable;

@Traced
public class TestBean implements Serializable {

    public TestBean() {}


    public void justTesting() {
        System.out.println("In testing method");
    }

}
