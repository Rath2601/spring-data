package com.practice.spring.data.spring_jpa_tx.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
//@DiscriminatorValue("UPI")
public class UpiPayment extends Payment {
    private String upiId; // This will be NULL for Card rows

    public UpiPayment() {
        super();
    }

    public UpiPayment(Long id, Double amount, String upiId) {
        super(id, amount);
        this.upiId = upiId;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
