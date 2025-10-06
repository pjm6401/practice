package com.project.common;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private int amount;
}
