package com.project.api.service.exceptionHandler;


import com.project.common.Response;
import com.project.common.status.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Response> handleOrderNotFoundException(OrderNotFoundException ex) {
        // ex.getOrderId()를 통해 실제 실패한 orderId를 가져올 수 있음
        Response errorResponse = new Response(ex.getOrderId(), OrderStatus.ERROR, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<Response> handleMerchantNotFoundException(MerchantNotFoundException ex) {
        // ex.getOrderId()를 통해 실제 실패한 orderId를 가져올 수 있음
        Response errorResponse = new Response(ex.getOrderId(), OrderStatus.ERROR, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserAccountNotFoundException.class)
    public ResponseEntity<Response> handleUserAccountNotFoundException(UserAccountNotFoundException ex) {
        // ex.getOrderId()를 통해 실제 실패한 orderId를 가져올 수 있음
        Response errorResponse = new Response(ex.getOrderId(), OrderStatus.ERROR, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
