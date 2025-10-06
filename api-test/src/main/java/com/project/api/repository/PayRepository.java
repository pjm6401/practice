package com.project.api.repository;



import com.project.api.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {
    // findById(Long orderId) 메서드가 자동으로 제공됩니다.
}