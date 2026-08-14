package com.ecommerce.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_number_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderNumberSequence {

    @Id
    private Integer id;

    @Column(nullable = false)
    private Long nextNumber;
}