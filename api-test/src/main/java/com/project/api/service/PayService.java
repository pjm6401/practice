package com.project.api.service;


import com.project.api.entity.Merchant;
import com.project.api.entity.Pay;
import com.project.api.entity.UserAccount;
import com.project.api.repository.MerchantRepository;
import com.project.api.repository.PayRepository;
import com.project.api.repository.UserAccountRepository;
import com.project.api.service.exceptionHandler.MerchantNotFoundException;
import com.project.api.service.exceptionHandler.OrderNotFoundException;
import com.project.api.service.exceptionHandler.UserAccountNotFoundException;
import com.project.common.Order;
import com.project.common.status.OrderStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
@RequiredArgsConstructor
@Transactional
public class PayService {

    private final PayRepository payRepository;
    private final MerchantRepository merchantRepository;
    private final UserAccountRepository userAccountRepository;

    public void createPayment(Order order) {
        UserAccount user = userAccountRepository.findById(order.getRequest().getUserId())
                .orElseThrow(() -> new UserAccountNotFoundException(order.getRequest().getOrderId(),"User not found: " + order.getRequest().getUserId()));
        Merchant merchant = merchantRepository.findById(order.getRequest().getMerchantId())
                .orElseThrow(() -> new MerchantNotFoundException(order.getRequest().getOrderId(),"Merchant not found: " + order.getRequest().getMerchantId()));

        Pay newPay = Pay.builder()
                .orderId(order.getRequest().getOrderId())
                .userAccount(user)
                .merchant(merchant)
                .amount(order.getRequest().getAmount())
                .status(order.getStatus())
                .build();

        payRepository.save(newPay);
    }

    public Order selectPayment(Order order){
        userAccountRepository.findById(order.getRequest().getUserId())
                .orElseThrow(() -> new UserAccountNotFoundException(order.getRequest().getOrderId(),"User not found: " + order.getRequest().getUserId()));
        merchantRepository.findById(order.getRequest().getMerchantId())
                .orElseThrow(() -> new MerchantNotFoundException(order.getRequest().getOrderId(),"Merchant not found: " + order.getRequest().getMerchantId()));
        Pay pay = payRepository.findById(order.getRequest().getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(order.getRequest().getOrderId(),"Order not found: " + order.getRequest().getOrderId()));


        if(pay.getStatus().canTransitionTo(OrderStatus.PAID)) {
            pay.setStatusPaid();
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.ERROR);
        }
        return order;
    }
}
