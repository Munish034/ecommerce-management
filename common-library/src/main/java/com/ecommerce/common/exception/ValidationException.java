package com.ecommerce.common.exception;

import com.ecommerce.common.enums.ErrorCode;

public class ValidationException extends BusinessException {

    public ValidationException(String message) {
        super(message, ErrorCode.VALIDATION_FAILED);
    }
}