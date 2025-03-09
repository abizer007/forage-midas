package com.jpmc.midascore; // Replace with actual package

import com.jpmc.midascore.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${general.consumer.group-id}")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);
        // Placeholder for handling transaction
        // You are not required to do anything else for this task
    }
}