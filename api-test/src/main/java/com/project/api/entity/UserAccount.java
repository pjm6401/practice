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
@Table(name = "user_account") // 테이블 이름을 'user_account'로 지정
public class UserAccount {

    @Id // 기본 키(Primary Key) 지정
    private Long userId;

    @Column(nullable = false, length = 100)
    private String userName;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY)
    private List<Pay> payments = new ArrayList<>();

    public void updateUserName(String userName) {
        this.userName = userName;
    }
}
