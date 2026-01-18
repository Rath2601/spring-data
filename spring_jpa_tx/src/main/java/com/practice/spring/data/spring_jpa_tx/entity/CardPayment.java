package com.practice.spring.data.spring_jpa_tx.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
//@DiscriminatorValue("CARD")
public class CardPayment extends Payment {
    private String cardNumber; // This will be NULL for UPI rows

    public CardPayment() {
        super();
    }
    public CardPayment(Long id, Double amount, String cardNumber) {
        super(id, amount);
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}

