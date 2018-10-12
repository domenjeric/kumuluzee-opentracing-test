package com.kumuluz.ee.opentracingtest.orders;


import java.util.ArrayList;
import java.util.List;

public class Database {
    private static List<Order> orders = new ArrayList<Order>();

    public static List<Order> getOrders() {
        return orders;
    }

    public static Order getOrder(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId))
                return order;
        }

        return null;
    }

    public static void addOrder(Order order) {
        orders.add(order);
    }

    public static void deleteOrder(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                orders.remove(order);
                break;
            }
        }
    }
}
