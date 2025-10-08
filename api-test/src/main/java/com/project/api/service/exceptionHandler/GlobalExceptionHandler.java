package com.project.api.service.exceptionHandler;


import com.project.common.Response;
import com.project.common.status.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();


        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // 에러가 발생한 필드 이름
            String errorMessage = error.getDefaultMessage();  // 어노테이션에 설정한 message 값
            errors.put(fieldName, errorMessage);
        });

        // 400 Bad Request와 함께 필드별 에러 메시지를 응답
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
