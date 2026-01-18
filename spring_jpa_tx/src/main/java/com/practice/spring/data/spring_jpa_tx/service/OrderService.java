package com.practice.spring.data.spring_jpa_tx.service;

import com.practice.spring.data.spring_jpa_tx.dto.OrderDto;
import com.practice.spring.data.spring_jpa_tx.entity.Order;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.practice.spring.data.spring_jpa_tx.repository.OrderCustomRepository;
import com.practice.spring.data.spring_jpa_tx.repository.OrderRepository;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

@Service
public class OrderService {

    private final PlatformTransactionManager transactionManager;
    private final OrderRepository repo;
    private final OrderCustomRepository dao;
    private final EntityManager em;

    public OrderService(PlatformTransactionManager transactionManager,
                        OrderRepository repo,
                        OrderCustomRepository dao,
                        EntityManager em) {
        this.transactionManager = transactionManager;
        this.repo = repo;
        this.dao = dao;
        this.em = em;
    }

    /* ---------------- ENTITY STATE DEMOS ---------------- */

    // TRANSIENT → PERSISTENT → FLUSH
// @Transactional
    public Order persistAndFlush(String status) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transactionStatus = transactionManager.getTransaction(def);

        Order order = new Order();  //TRANSIENT STATE
//        order.setId(1L); // manually setting an ID makes it detached
        order.setStatus(status);

        em.persist(order);   // PERSISTENT STATE

        transactionManager.commit(transactionStatus);
        return order;
    }

    // DIRTY CHECKING
    @Transactional
    public Order dirtyChecking(Long id, String newStatus) {
        Order order = em.find(Order.class, id); // managed
        order.setStatus(newStatus);             // dirty
        // flush happens automatically at commit
        return order;
    }

    // DETACH
    @Transactional
    public OrderDto detachDemo(Long id) {
        Order order = em.find(Order.class, id);
        em.detach(order);          // detached
        order.setStatus("DETACHED_NO_EFFECT");
        return new OrderDto(order.getOrderId(), order.getStatus());
    }

    // MERGE
    @Transactional
    public OrderDto mergeDemo(Long id, String status) {
        Order detached = em.find(Order.class, id);
        em.detach(detached);

        detached.setStatus(status);

        Order merged = em.merge(detached); // new managed instance
        return new OrderDto(merged.getOrderId(), merged.getStatus());
    }

    // REMOVE
    @Transactional
    public void removeOrder(Long id) {
        Order order = em.find(Order.class, id);
        em.remove(order);
//        order.setStatus("REMOVED_OBJ");
//        em.persist(order);
    }

    /* ---------------- TRANSACTION PROPAGATION ---------------- */

    @Transactional(propagation = Propagation.REQUIRED)
    public void requiredTx() {
        repo.save(new Order());
        requiresNewTx();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void requiresNewTx() {
        repo.save(new Order());
    }

    /* ---------------- QUERY DEMOS ---------------- */

    public List<OrderDto> derivedQuery(String status) {
        return repo.findByStatus(status)
                .stream()
                .map(o -> new OrderDto(o.getOrderId(), o.getStatus()))
                .toList();
    }

    public List<OrderDto> jpqlQuery(String status) {
        return repo.jpqlQuery(status)
                .stream()
                .map(o -> new OrderDto(o.getOrderId(), o.getStatus()))
                .toList();
    }

    public List<OrderDto> nativeQuery(String status) {
        return repo.nativeQuery(status)
                .stream()
                .map(o -> new OrderDto(o.getOrderId(), o.getStatus()))
                .toList();
    }

    public List<OrderDto> criteriaQuery(String status) {
        return dao.criteriaQuery(status)
                .stream()
                .map(o -> new OrderDto(o.getOrderId(), o.getStatus()))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDto readOnly(Long id) {
        Order order = repo.findById(id).orElseThrow();
        return new OrderDto(order.getOrderId(), order.getStatus());
    }
}

