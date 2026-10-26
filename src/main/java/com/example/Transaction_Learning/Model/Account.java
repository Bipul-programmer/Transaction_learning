package com.example.Transaction_Learning.Model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;

    private BigDecimal balance;

    @Version
    private Long version;

    protected Account() {
        // Required by JPA
    }

    public Account(String owner, BigDecimal balance) {
        this.owner = owner;
        this.balance = balance;
    }

    public void debit(BigDecimal amt) {
        if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (balance.compareTo(amt) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        this.balance = this.balance.subtract(amt);
    }

    public void credit(BigDecimal amt) {
        if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.balance = this.balance.add(amt);
    }

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }
}