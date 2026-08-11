package com.ecommerce.authservice.service.impl;

import com.ecommerce.authservice.dto.request.CreateAddressRequest;
import com.ecommerce.authservice.dto.request.UpdateAddressRequest;
import com.ecommerce.authservice.dto.response.AddressResponse;
import com.ecommerce.authservice.entity.Address;
import com.ecommerce.authservice.entity.User;
import com.ecommerce.authservice.mapper.AddressMapper;
import com.ecommerce.authservice.repository.AddressRepository;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.service.AddressService;
import com.ecommerce.common.enums.ErrorCode;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponse createAddress(
            CreateAddressRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        User user = getUser(userId);

        Address address = addressMapper.toEntity(request);

        address.setUser(user);

        boolean makeDefault =
                Boolean.TRUE.equals(request.getDefaultAddress());

        if (makeDefault) {
            removeExistingDefaultAddress(userId);
        }

        address.setDefaultAddress(makeDefault);

        Address savedAddress =
                addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {

        Long userId = SecurityUtils.getCurrentUserId();

        return addressRepository.findByUserId(userId)
                .stream()
                .map(addressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getMyAddress(Long addressId) {

        Long userId = SecurityUtils.getCurrentUserId();

        Address address =
                getOwnedAddress(addressId, userId);

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse updateAddress(
            Long addressId,
            UpdateAddressRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        Address address =
                getOwnedAddress(addressId, userId);

        boolean makeDefault =
                Boolean.TRUE.equals(request.getDefaultAddress());

        if (makeDefault) {
            removeExistingDefaultAddress(
                    userId,
                    addressId
            );
        }

        addressMapper.updateEntity(request, address);

        address.setDefaultAddress(makeDefault);

        Address updatedAddress =
                addressRepository.save(address);

        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(Long addressId) {

        Long userId = SecurityUtils.getCurrentUserId();

        Address address =
                getOwnedAddress(addressId, userId);

        addressRepository.delete(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId) {

        Long userId = SecurityUtils.getCurrentUserId();

        Address address =
                getOwnedAddress(addressId, userId);

        removeExistingDefaultAddress(
                userId,
                addressId
        );

        address.setDefaultAddress(true);

        Address savedAddress =
                addressRepository.save(address);

        return addressMapper.toResponse(savedAddress);
    }

    private Address getOwnedAddress(
            Long addressId,
            Long userId) {

        return addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address not found.",
                                ErrorCode.RESOURCE_NOT_FOUND
                        ));
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found.",
                                ErrorCode.USER_NOT_FOUND
                        ));
    }

    private void removeExistingDefaultAddress(
            Long userId) {

        addressRepository
                .findByUserIdAndDefaultAddressTrue(userId)
                .ifPresent(address -> {
                    address.setDefaultAddress(false);
                    addressRepository.save(address);
                });
    }

    private void removeExistingDefaultAddress(
            Long userId,
            Long addressId) {

        addressRepository
                .findByUserIdAndDefaultAddressTrue(userId)
                .filter(address ->
                        !address.getId().equals(addressId))
                .ifPresent(address -> {
                    address.setDefaultAddress(false);
                    addressRepository.save(address);
                });
    }
}