package com.ecommerce.common.exception;

import com.ecommerce.common.enums.ErrorCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message, ErrorCode productNotFound) {
        super(message, ErrorCode.RESOURCE_NOT_FOUND);
    }
}