package com.project.api.repository;

import com.project.api.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    // findById(Long merchantId) 메서드가 자동으로 제공됩니다.
}