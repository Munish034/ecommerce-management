package com.ecommerce.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {

        return new NewTopic(
                com.ecommerce.common.constant.KafkaTopics.ORDER_CREATED,
                3,
                (short) 1);
    }

    @Bean
    public NewTopic orderCancelledTopic() {

        return new NewTopic(
                com.ecommerce.common.constant.KafkaTopics.ORDER_CANCELLED,
                3,
                (short) 1);
    }

}