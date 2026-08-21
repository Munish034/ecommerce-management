package com.ecommerce.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String title;

    @Column(length = 1000)
    private String message;

    private String type;

    @Builder.Default
    private Boolean readStatus = false;

    private LocalDateTime createdAt;
}