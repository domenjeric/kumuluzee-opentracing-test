package com.kumuluz.ee.opentracingtest.customers;


import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("customers")
public class CustomerResource {

    @Inject
    private TestBean testBean;

    @GET
    public Response getAllCustomers() {
        List<Customer> customers = Database.getCustomers();
        return Response.ok(customers).build();
    }

    @GET
    @Path("{customerId}")
    public Response getCustomer(@PathParam("customerId") String customerId) {
        Customer customer = Database.getCustomer(customerId);
        return customer != null
                ? Response.ok(customer).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewCustomer(Customer customer) {
        Database.addCustomer(customer);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {
        Database.deleteCustomer(customerId);
        return Response.noContent().build();
    }

    @GET
    @Path("{customerId}/orders")
    public Response getCustomerOrders(@PathParam("customerId") String customerId) {

        //Just to test tracing
        testBean.justTesting();

        try {
            OkHttpClient client = new OkHttpClient();
            Tracer tracer = GlobalTracer.get();
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("localhost")
                    .port(3001)
                    .addPathSegment("v1")
                    .addPathSegment("orders")
                    .build();

            Request.Builder requestBuilder = new Request.Builder().url(url);

            Tags.SPAN_KIND.set(tracer.activeSpan(), Tags.SPAN_KIND_CLIENT);
            Tags.HTTP_METHOD.set(tracer.activeSpan(), "GET");
            Tags.HTTP_URL.set(tracer.activeSpan(), url.toString());

            tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, new RequestBuilderCarrier(requestBuilder));

            Request request = requestBuilder.build();
            okhttp3.Response response = client.newCall(request).execute();

            if (response.code() != 200) {
                throw new RuntimeException("Response error: " + response);
            }

            return Response.ok(response.body().string()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

