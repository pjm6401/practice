package com.project.api.service.exceptionHandler;

import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException {

    private final Long orderId;

    public OrderNotFoundException(Long orderId, String message) {
        super(message);
        this.orderId = orderId;
    }
}