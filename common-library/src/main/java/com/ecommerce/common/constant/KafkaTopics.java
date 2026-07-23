package com.ecommerce.common.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String ORDER_CREATED = "order-created";

    public static final String ORDER_CANCELLED = "order-cancelled";

    // Dead Letter Topics
    public static final String ORDER_CREATED_DLT = "order-created-dlt";
    public static final String ORDER_CANCELLED_DLT = "order-cancelled-dlt";
}