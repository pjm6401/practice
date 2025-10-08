package com.project.api.repository;



import com.project.api.entity.Pay;
import com.project.common.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Long> {
    List<Pay> findByUserAccount_UserId(Long userId);
    List<Pay> findByUserAccount_UserIdAndStatus(Long userId, OrderStatus status);
}