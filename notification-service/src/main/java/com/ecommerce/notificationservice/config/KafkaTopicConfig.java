package com.ecommerce.notificationservice.config;

import com.ecommerce.common.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic(KafkaTopics.ORDER_CREATED, 1, (short) 1);
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return new NewTopic(KafkaTopics.ORDER_CANCELLED, 1, (short) 1);
    }

    @Bean
    public NewTopic orderCreatedDLT() {
        return new NewTopic(KafkaTopics.ORDER_CREATED_DLT, 1, (short) 1);
    }

    @Bean
    public NewTopic orderCancelledDLT() {
        return new NewTopic(KafkaTopics.ORDER_CANCELLED_DLT, 1, (short) 1);
    }
}