package com.jpmc.midascore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionKafkaListener {



    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    public TransactionKafkaListener(TransactionService transactionService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "${general.consumer.group-id}")
    public void consumeTransaction(String transactionMessage) {
        try {
            JsonNode transactionJson = objectMapper.readTree(transactionMessage);
            String senderId = transactionJson.get("senderId").asText();
            String recipientId = transactionJson.get("recipientId").asText();
            double amount = transactionJson.get("amount").asDouble();

            transactionService.processTransaction(senderId, recipientId, amount);
        } catch (Exception e) {
            logger.error("Exception occurred while processing Kafka message", e);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(TransactionKafkaListener.class);
}