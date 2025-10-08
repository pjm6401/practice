package com.project.api.controller;

import com.project.api.entity.Pay;
import com.project.api.entity.UserAccount;
import com.project.api.service.PayService;
import com.project.api.service.UserService;
import com.project.common.Order;
import com.project.common.Request;
import com.project.common.Response;
import com.project.common.status.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiTestController {

    private final PayService payService;
    private final UserService userService;

    @PostMapping("/pay")
    public ResponseEntity<Response> pay(@Valid @RequestBody Request request) {
        Order order = new Order(request);

        System.out.println(order.getRequest().getOrderId() +" "+order.getRequest().getUserId() +" "+order.getRequest().getAmount() +" "+order.getStatus());

        payService.createPayment(order);

        Response responseBody = new Response(order.getRequest().getOrderId(), order.getStatus()," Payment initiated");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/paid")
    public ResponseEntity<Response> paid(@RequestBody Request request) {
        Order order = new Order(request);

        order = payService.selectPayment(order);

        System.out.println(order.getRequest().getOrderId() +" "+order.getRequest().getUserId() +" "+order.getRequest().getAmount() +" "+order.getStatus());

        Response responseBody = new Response(order.getRequest().getOrderId(), order.getStatus()," Payment completed");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Response> cancel(@RequestBody Request request) {

        OrderStatus status = OrderStatus.PAID;
        if (status.canTransitionTo(OrderStatus.CANCELED)) {
            status = OrderStatus.CANCELED;
        }

        Response responseBody = new Response(request.getOrderId(), status,"Cancellation completed");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/close")
    public ResponseEntity<Response> close(@RequestBody Request request) {

        OrderStatus status = OrderStatus.CREATED;
        if (status.canTransitionTo(OrderStatus.CLOSE)) {
            status = OrderStatus.CLOSE;
        }

        Response responseBody = new Response(request.getOrderId(), status,"Closure completed");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/refund")
    public ResponseEntity<Response> refund(@RequestBody Request request) {

        OrderStatus status = OrderStatus.PAID;
        if (status.canTransitionTo(OrderStatus.REFUND)) {
            status = OrderStatus.REFUND;
        }

        Response responseBody = new Response(request.getOrderId(), status,"Refund completed");

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/cancelError")
    public ResponseEntity<Response> cancelError(@RequestBody Request request) {

        OrderStatus status = OrderStatus.CREATED;
        if (status.canTransitionTo(OrderStatus.CANCELED)) {
            status = OrderStatus.CANCELED;
        }else{
            status = OrderStatus.ERROR;
        }

        Response responseBody = new Response(request.getOrderId(), status,"Cancellation failed");

        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }

    @GetMapping("/pays/{userId}")
    public ResponseEntity<List<Response>> getPaymentsByUserId(@PathVariable Long userId,
                                                         @RequestParam(required = false) OrderStatus status)
    {
        List<Pay> list = userService.findPaymentsByUserId(userId, status);
        List<Response> responseList = new ArrayList<>();

        for(Pay pay : list){
            Response response = new Response(pay.getOrderId(), pay.getStatus(), "Payment record");
            responseList.add(response);
        }
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
    @GetMapping("/info/{userId}")
    public ResponseEntity<UserAccount> getUserInfo(@PathVariable Long userId)
    {

        return new ResponseEntity<>(userService.findUserById(userId), HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserAccount> updateUserInfo(@PathVariable Long userId, @RequestBody Request request)
    {

        return new ResponseEntity<>(userService.updateUserById(userId,request.getUserName()), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUserInfo(@PathVariable Long userId)
    {
        userService.deleteUserById(userId);
        return new ResponseEntity<>("Delete "+ userId, HttpStatus.OK);
    }
}