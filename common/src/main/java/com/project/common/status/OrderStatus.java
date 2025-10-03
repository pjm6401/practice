package com.project.common.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum OrderStatus {
    ERROR(Collections.emptyList()),
    CANCELED(Collections.emptyList()),
    CLOSE(Collections.emptyList()),
    REFUND(Collections.emptyList()),
    PAID(Arrays.asList(REFUND, CANCELED,ERROR)),
    CREATED(Arrays.asList(PAID, CLOSE,ERROR));

    private final List<OrderStatus> nextStates;

    OrderStatus(List<OrderStatus> nextStates) {
        this.nextStates = nextStates;
    }

    public boolean canTransitionTo(OrderStatus next) {
        return nextStates.contains(next);
    }
}

