package com.project.common;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @NotNull(message = "orderId cannot be null")
    private Long orderId;
    @Max(value = 9999, message = "userId must be less than or equal to 9999")
    @Min(value = 1000, message = "userId must be greater than or equal to 1000")
    private Long userId;

    @Size(min = 2, max = 30, message = "userName must be between 2 and 30 characters")
    private String userName;

    @NotNull(message = "merchantId cannot be null")
    @Min(value = 1000, message = "merchantId는 4자리 숫자여야 합니다.")
    @Max(value = 9999, message = "merchantId는 4자리 숫자여야 합니다.")
    private Long merchantId;

    @NotNull(message = "merchantId cannot be null")
    @Min(value = 1, message = "금액은 1원 이상 이어야 합니다.")
    private int amount;
}
