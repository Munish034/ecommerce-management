package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.request.CreateAddressRequest;
import com.ecommerce.authservice.dto.request.UpdateAddressRequest;
import com.ecommerce.authservice.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(CreateAddressRequest request);

    List<AddressResponse> getMyAddresses();

    AddressResponse getMyAddress(Long addressId);

    AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request
    );

    void deleteAddress(Long addressId);

    AddressResponse setDefaultAddress(Long addressId);
}