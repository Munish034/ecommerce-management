package com.ecommerce.authservice.controller;

import com.ecommerce.authservice.dto.request.CreateAddressRequest;
import com.ecommerce.authservice.dto.request.UpdateAddressRequest;
import com.ecommerce.authservice.dto.response.AddressResponse;
import com.ecommerce.authservice.service.AddressService;
import com.ecommerce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    @GetMapping("/test")
    public String  test (){


        return "test";

    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> createAddress(
            @Valid @RequestBody CreateAddressRequest request) {
        System.out.println("createAddress controller");
        AddressResponse response =
                addressService.createAddress(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Address created successfully.",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Addresses fetched successfully.",
                        addressService.getMyAddresses()
                )
        );
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getMyAddress(
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address fetched successfully.",
                        addressService.getMyAddress(addressId)
                )
        );
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody UpdateAddressRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address updated successfully.",
                        addressService.updateAddress(
                                addressId,
                                request
                        )
                )
        );
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long addressId) {

        addressService.deleteAddress(addressId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Address deleted successfully.",
                        null
                )
        );
    }

    @PutMapping("/{addressId}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long addressId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Default address updated successfully.",
                        addressService.setDefaultAddress(addressId)
                )
        );
    }
}