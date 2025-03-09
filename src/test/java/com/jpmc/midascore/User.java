package com.jpmc.midascore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    private double balance;

    public double getBalance() {
        return balance;
    }

    public int setBalance(double v) {
        return 0;
    }

    // constructors, getters, setters
}