package com.ecommerce.cartservice.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {

    private Long id;

    private String fullName;

    private String mobile;

    private String addressLine1;

    private String addressLine2;

    private String landmark;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String addressType;

    private Boolean defaultAddress;
}