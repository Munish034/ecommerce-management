package com.ecommerce.orderservice.service.imp;



import com.ecommerce.orderservice.entity.Order;
import com.ecommerce.orderservice.entity.OrderItem;
import com.ecommerce.orderservice.service.PricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingServiceImpl implements PricingService {

    @Override
    public void calculatePrice(Order order) {

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {

            total = total.add(item.getTotalPrice());

        }

        order.setTotalAmount(total);

        order.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal tax = total.multiply(new BigDecimal("0.18"));

        order.setTaxAmount(tax);

        order.setFinalAmount(total.add(tax));
    }
}