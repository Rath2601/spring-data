package com.practice.spring.data.spring_jpa_tx.repository;

import com.practice.spring.data.spring_jpa_tx.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderCustomRepository {

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    public void persist(Order order) {
        em.persist(order);
    }

    public List<Order> criteriaQuery(String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);

        Root<Order> root = cq.from(Order.class);
        cq.select(root).where(cb.equal(root.get("status"), status));

        return em.createQuery(cq).getResultList();
    }
}

