package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = {
        "kafka.topic=my-test-topic",
        "kafka.consumer.group-id=midas-core-group"
})
class TaskTwoTests {

    private static final Logger logger = LoggerFactory.getLogger(TaskTwoTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private FileLoader fileLoader;

    private final List<String> receivedMessages = new ArrayList<>();
    private CountDownLatch latch;

    @Test
    void task_two_verifier() throws InterruptedException {
        String[] transactionLines = fileLoader.loadStrings("/test_data/poiuytrewq.uiop");

        if (transactionLines == null || transactionLines.length == 0) {
            logger.error("No transactions loaded from the file '/test_data/poiuytrewq.uiop'. Test cannot proceed.");
            return;
        }

        // Configure CountDownLatch according to the number of transactions expected
        latch = new CountDownLatch(transactionLines.length);

        for (String transactionLine : transactionLines) {
            try {
                kafkaProducer.send(transactionLine);
                logger.info("Successfully sent transaction line: {}", transactionLine);
            } catch (Exception e) {
                logger.error("Failed to send transaction line: " + transactionLine, e);
            }
        }

        // Wait for Kafka listener to receive all transactions
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        if (!completed) {
            logger.warn("Timed out waiting for transactions to be processed.");
        }

        // Verify that all messages have been received
        assertEquals(transactionLines.length, receivedMessages.size(), "Not all transactions were received.");

        // Log received transactions for verification
        receivedMessages.forEach(transaction -> logger.info("Received transaction: {}", transaction));
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.consumer.group-id}")
    public void listen(String message) {
        logger.info("Received message via KafkaListener: {}", message);
        receivedMessages.add(message);
        latch.countDown();
    }
}