package com.jpmc.midascore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "sender_user_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_user_id")
    private User recipient;

    private double amount;

    private LocalDateTime timestamp;

    public void setSender(User sender) {
    }

    public void setRecipient(User recipient) {
    }

    public void setAmount(double amount) {
    }

    public void setTimestamp(LocalDateTime now) {

    }

    public void setIncentive(double incentiveAmount) {
    }

    // constructors, getters, setters
}