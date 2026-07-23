package com.ecommerce.orderservice.specification;

import com.ecommerce.orderservice.dto.request.OrderSearchRequest;
import com.ecommerce.orderservice.entity.Order;
import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class OrderSpecification {



    public static Specification<Order> search(OrderSearchRequest request) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (request.getOrderNumber() != null
                    && !request.getOrderNumber().isBlank()) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("orderNumber"),
                                request.getOrderNumber()));
            }

            if (request.getOrderStatus() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("orderStatus"),
                                request.getOrderStatus()));
            }

            if (request.getPaymentStatus() != null) {

                predicates.add(
                        criteriaBuilder.equal(
                                root.get("paymentStatus"),
                                request.getPaymentStatus()));
            }

            if (request.getMinAmount() != null) {

                predicates.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("finalAmount"),
                                request.getMinAmount()));
            }

            if (request.getMaxAmount() != null) {

                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("finalAmount"),
                                request.getMaxAmount()));
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0]));
        };
    }
}