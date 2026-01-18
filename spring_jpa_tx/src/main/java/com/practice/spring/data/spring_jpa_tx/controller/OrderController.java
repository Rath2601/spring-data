package com.practice.spring.data.spring_jpa_tx.controller;

import com.practice.spring.data.spring_jpa_tx.dto.OrderDto;
import com.practice.spring.data.spring_jpa_tx.entity.*;
import com.practice.spring.data.spring_jpa_tx.repository.PaymentRepository;
import com.practice.spring.data.spring_jpa_tx.service.OrderService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders") // base url
public class OrderController {

    private final OrderService service;
    private final PaymentRepository paymentRepository;
    private final EntityManager entityManager;

    public OrderController(OrderService service, PaymentRepository paymentRepository, EntityManager entityManager) {
        this.service = service;
        this.paymentRepository = paymentRepository;
        this.entityManager = entityManager;
    }

    /* -------- ENTITY STATES -------- */

    @PostMapping("/persist")
    public Order persist(@RequestParam String status) {
        return service.persistAndFlush(status);
    }

    @PutMapping("/{id}/dirty")
    public Order dirtyCheck(@PathVariable Long id,
                            @RequestParam String status) {
        Order order = service.dirtyChecking(id, status);
        order.getItems().size();
        return order;

    }

    @PutMapping("/{id}/detach")
    public OrderDto detach(@PathVariable Long id) {
        return service.detachDemo(id);
    }

    @PutMapping("/{id}/merge")
    public OrderDto merge(@PathVariable Long id,
                       @RequestParam String status) {
        return service.mergeDemo(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.removeOrder(id);
    }

    /* -------- TRANSACTION PROPAGATION -------- */

    @PostMapping("/tx/required")
    public void requiredTx() {
        service.requiredTx();
    }

    /* -------- QUERIES -------- */

    @GetMapping("/derived")
    public List<OrderDto> derived(@RequestParam String status) {
        return service.derivedQuery(status);
    }

    @GetMapping("/jpql")
    public List<OrderDto> jpql(@RequestParam String status) {
        return service.jpqlQuery(status);
    }

    @GetMapping("/native")
    public List<OrderDto> nativeQ(@RequestParam String status) {
        return service.nativeQuery(status);
    }

    @GetMapping("/criteria")
    public List<OrderDto> criteria(@RequestParam String status) {
        return service.criteriaQuery(status);
    }

    @GetMapping("/{id}")
    public OrderDto find(@PathVariable Long id) {
        return service.readOnly(id);
    }

    @PostMapping
    public List<Payment> savePayment() {
        return List.of(paymentRepository.save( new CardPayment(101L, 1000d, "############5764")),
                paymentRepository.save( new UpiPayment(102L, 1000d, "***********okhdfc")));
    }

    @Transactional
    @GetMapping("/cascade-marathon")
    public void runMarathon() {

        Order order = new Order();
        order.setStatus("CREATED");
        order.setCheckValue("Verify_1");


        OrderItem item = new OrderItem();
        item.setOrderId(1L);
        item.setProduct("Product_one");
        item.setProduct("Product_two");
//        item.setOrder(order);
        order.addItem(item);


        entityManager.persist(order);

//        Order fetchedOrder = entityManager.find(Order.class, 1);
//        entityManager.detach(fetchedOrder);

//        fetchedOrder.getItems().removeFirst(); // making change in detached object
//        entityManager.persist(fetchedOrder);

//        Order reloadedOrder = entityManager.find(Order.class, 1); // fresh fetch from DB

    }
}

