package com.project.api.service.exceptionHandler;


import lombok.Getter;

@Getter
public class UserAccountNotFoundException extends RuntimeException{
    private final Long orderId;

    public UserAccountNotFoundException(Long orderId, String message) {
        super(message);
        this.orderId = orderId;
    }
}
