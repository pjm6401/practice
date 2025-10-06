package com.project.api.repository;

import com.project.api.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    // findById(Long userId) 메서드가 자동으로 제공됩니다.
}