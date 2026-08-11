package com.ecommerce.authservice.dto.response;

import com.ecommerce.authservice.enums.AddressType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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

    private AddressType addressType;

    private Boolean defaultAddress;
}