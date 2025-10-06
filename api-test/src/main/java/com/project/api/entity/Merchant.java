package com.project.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "merchant")
public class Merchant {

    @Id // 기본 키(Primary Key) 지정
    private Long merchantId;

    @Column(nullable = false, length = 200)
    private String merchantName;

    @OneToMany(mappedBy = "merchant", fetch = FetchType.LAZY)
    private List<Pay> payments = new ArrayList<>();
}