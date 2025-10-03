package com.project.api.controller;



import com.project.common.Order;
import com.project.common.Request;
import com.project.common.Response;
import com.project.common.status.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiTestController {

    @PostMapping("/pay")
    public ResponseEntity<Response> pay(@RequestBody Request request) {
        Order order = new Order(request);

        System.out.println(order.getRequest().getOrderId() +" "+order.getRequest().getUserId() +" "+order.getRequest().getAmount() +" "+order.getStatus());

        Response responseBody = new Response(order.getRequest().getOrderId(), order.getStatus());

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/cancel")
    public ResponseEntity<Response> cancel(@RequestBody Request request) {

        OrderStatus status = OrderStatus.PAID;
        if (status.canTransitionTo(OrderStatus.CANCELED)) {
            status = OrderStatus.CANCELED;
        }

        Response responseBody = new Response(request.getOrderId(), status);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/close")
    public ResponseEntity<Response> close(@RequestBody Request request) {

        OrderStatus status = OrderStatus.CREATED;
        if (status.canTransitionTo(OrderStatus.CLOSE)) {
            status = OrderStatus.CLOSE;
        }

        Response responseBody = new Response(request.getOrderId(), status);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/refund")
    public ResponseEntity<Response> refund(@RequestBody Request request) {

        OrderStatus status = OrderStatus.PAID;
        if (status.canTransitionTo(OrderStatus.REFUND)) {
            status = OrderStatus.REFUND;
        }

        Response responseBody = new Response(request.getOrderId(), status);

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

        Response responseBody = new Response(request.getOrderId(), status);

        return new ResponseEntity<>(responseBody, HttpStatus.CONFLICT);
    }
}