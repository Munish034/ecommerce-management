package com.ecommerce.notificationservice.config;

import com.ecommerce.common.constant.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaErrorHandlerConfig {

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> {

                            if (KafkaTopics.ORDER_CREATED.equals(record.topic())) {
                                return new TopicPartition(
                                        KafkaTopics.ORDER_CREATED_DLT,
                                        record.partition()
                                );
                            }

                            if (KafkaTopics.ORDER_CANCELLED.equals(record.topic())) {
                                return new TopicPartition(
                                        KafkaTopics.ORDER_CANCELLED_DLT,
                                        record.partition()
                                );
                            }

                            return new TopicPartition(record.topic(), record.partition());
                        });

        FixedBackOff backOff = new FixedBackOff(
                2000L,   // 2 seconds
                3        // Retry 3 times
        );

        return new DefaultErrorHandler(recoverer, backOff);
    }
}