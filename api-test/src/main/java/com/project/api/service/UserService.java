package com.project.api.service;


import com.project.api.entity.Pay;
import com.project.api.entity.UserAccount;
import com.project.api.repository.PayRepository;
import com.project.api.repository.UserAccountRepository;
import com.project.common.status.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final PayRepository payRepository;

    public List<Pay> findPaymentsByUserId(Long userId, OrderStatus status) {
        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        if (status == null) {
            // status가 없으면 userId로만 조회
            return payRepository.findByUserAccount_UserId(userId);
        } else {
            // status가 있으면 userId와 status로 조회
            return payRepository.findByUserAccount_UserIdAndStatus(userId, status);
        }
    }

    public UserAccount findUserById(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }
    @Transactional
    public UserAccount updateUserById(Long userId,String userName) {
        UserAccount userAccount = userAccountRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        userAccount.updateUserName(userName);

        return userAccount;
    }

    @Transactional
    public void deleteUserById(Long userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
        userAccountRepository.deleteById(userId);
    }
}
