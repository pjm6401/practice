package com.project.common;


import com.project.common.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private Long orderId;
    private OrderStatus status;
    private String message;
}
