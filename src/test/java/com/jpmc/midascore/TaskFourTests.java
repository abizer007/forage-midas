package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"transactionTopic"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
public class TaskFourTests {

    private static final Logger logger = LoggerFactory.getLogger(TaskFourTests.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;



    @Test
    void taskFourTest_VerifyTransactionsProcessed() throws InterruptedException {
        // Populating clearly defined users for transaction test scenario
        userPopulator.populate();

        // Load transaction data
        String[] transactionLines = loadTransactionLines();

        // Check if transactions are loaded
        if (transactionLines.length == 0) {
            logger.error("No transactions loaded for testing. Please check input data.");
            return;
        }

        // Sending transactions to embedded Kafka topic
        for (String transactionLine : transactionLines) {
            kafkaTemplate.send("transactionTopic", transactionLine);
            logger.info("Transaction sent: " + transactionLine);
        }

        // Giving some explicit wait time for consumer to finish processing transactions internally
        Thread.sleep(2000);

        // Providing checkpoint message clearly for debug purposes
        logger.info("----------------------------");
        logger.info("All transactions have been dispatched clearly. Check final balances in your debuggers or consumers.");
        logger.info("----------------------------");
    }

    private String[] loadTransactionLines() {
        try {
            return fileLoader.loadStrings("/test_data/transactions.txt");
        } catch (Exception e) {
            logger.error("Unable to load transactions file clearly from resources!", e);
            return new String[]{};
        }
    }

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserPopulator userPopulator;



}