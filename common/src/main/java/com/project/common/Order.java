package com.project.common;

import com.project.common.status.OrderStatus;
import lombok.Data;

@Data
public class Order {
    private Request request;

    private OrderStatus status; // 현재 상태

    public Order(Request request) {
        this.request = request;
        this.status = OrderStatus.CREATED; // ✅ 최초 상태는 무조건 CREATED
    }
}
