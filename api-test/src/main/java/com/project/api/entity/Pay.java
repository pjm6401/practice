package com.project.api.entity;

import com.project.common.status.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pay") // 테이블 이름을 'pay'로 명시
public class Pay {

    @Id // 기본 키(Primary Key) 지정
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY: Pay 조회 시 UserAccount는 필요할 때 조회
    @JoinColumn(name = "user_id") // DB 상의 외래 키 컬럼 이름 지정
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id") // DB 상의 외래 키 컬럼 이름 지정
    private Merchant merchant;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    public void setStatusPaid() {
        this.status = OrderStatus.PAID;
    }
    public void setStatusCanceled() {
        this.status = OrderStatus.CANCELED;
    }
    public void setStatusRefunded() {
        this.status = OrderStatus.REFUND;
    }
    public void setStatusClose() {
        this.status = OrderStatus.CLOSE;
    }
}
