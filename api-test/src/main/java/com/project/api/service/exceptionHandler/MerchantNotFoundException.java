package com.project.api.service.exceptionHandler;


import lombok.Getter;

@Getter
public class MerchantNotFoundException extends RuntimeException {
    private final Long orderId;


    public MerchantNotFoundException(Long orderId, String message) {
        super(message);
        this.orderId = orderId;
    }
}
