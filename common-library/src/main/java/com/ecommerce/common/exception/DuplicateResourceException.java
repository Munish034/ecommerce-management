package com.ecommerce.common.exception;


import com.ecommerce.common.enums.ErrorCode;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message, ErrorCode.DUPLICATE_RESOURCE);
    }
}