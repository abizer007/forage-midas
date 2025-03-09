package com.jpmc.midascore;

import com.jpmc.midascore.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private UserRepository1 userRepository1;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";

    @Transactional
    public void processTransaction(String senderId, String recipientId, double amount) {
        var senderOpt = userRepository1.findByUserId(senderId);
        var recipientOpt = userRepository1.findByUserId(recipientId);

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return; // invalid sender or recipient
        }

        User sender = senderOpt.get();
        var recipient = recipientOpt.get();

        if (sender.getBalance() < amount) {
            return; // insufficient balance
        }

        // Create Transaction object for incentive API
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(recipient);
        transaction.setAmount(amount);

        // Validate transaction (assuming validation logic is implemented)
        if (!validate(transaction)) {
            return; // validation failed
        }

        // Call incentives API
        Incentive incentive = restTemplate.postForObject(
                INCENTIVE_API_URL,
                transaction,
                Incentive.class
        );

        double incentiveAmount = incentive != null ? incentive.getAmount() : 0.0;

        // Update balances with incentive
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Persist the updated balances
        userRepository1.save(sender);
        userRepository1.save(recipient);

        // Record transaction
        TransactionRecord record = new TransactionRecord();
        record.setSender(sender);
        record.setRecipient(recipient);
        record.setAmount(amount);
        record.setIncentive(incentiveAmount);
        record.setTimestamp(LocalDateTime.now());

        transactionRecordRepository.save(record);
    }

    private boolean validate(Transaction transaction) {
        // Implement actual validation logic based on your requirements
        return true;
    }
}