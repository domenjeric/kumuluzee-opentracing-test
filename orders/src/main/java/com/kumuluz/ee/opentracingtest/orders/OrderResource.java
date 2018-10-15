package com.kumuluz.ee.opentracingtest.orders;


import com.kumuluz.ee.opentracingtest.tracing.Traced;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("orders")
@Traced
public class OrderResource {

    @GET
    public Response getAllOrders() {
        List<Order> orders = Database.getOrders();
        return Response.ok(orders).build();
    }

    @GET
    @Path("{orderId}")
    public Response getOrder(@PathParam("orderId") String orderId) {
        Order order = Database.getOrder(orderId);
        return order != null
                ? Response.ok(order).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response addNewOrder(Order order) {
        Database.addOrder(order);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{orderId}")
    public Response deleteOrder(@PathParam("orderId") String orderId) {
        Database.deleteOrder(orderId);
        return Response.noContent().build();
    }
}

