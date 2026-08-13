package com.ecommerce.cartservice.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryProductResponse<T>{

    private boolean success;
    private String message;
    private T data;
    private String timestamp;
}
