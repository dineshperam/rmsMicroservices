package com.rms.service;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.rms.exeptions.NotFoundException;
import com.rms.model.Transactions;
import com.rms.repository.TransactionRepository;

@Service
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private static final Logger logger = Logger.getLogger(TransactionService.class);

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    public List<Transactions> showTrans() {
        logger.info("Fetching all transactions.");
        List<Transactions> transactions = transactionRepository.findAll();
        if (transactions.isEmpty()) {
            logger.warn("No transactions found.");
            throw new NotFoundException("No transactions found.");
        }
        return transactions;
    }

    public List<Transactions> showTransByReceiver(int id) {
        logger.info("Fetching transactions for receiver with ID: " + id);
        List<Transactions> transactions = transactionRepository.findByReceiver(id);
        if (transactions.isEmpty()) {
            logger.warn("No Transactions for receiver with ID: " + id);
            throw new NotFoundException("No transactions found for user with ID: " + id);
        }
        return transactions;
    }

    public List<Transactions> showTransByManId(int id) {
        logger.info("Fetching transactions for manager with ID: " + id);
        List<Transactions> transactions = transactionRepository.findByManagerId(id);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for managerID: " + id);
            throw new NotFoundException("No transactions found for manager with ID: " + id);
        }
        return transactions;
    }

    public List<Transactions> getTransactionsByReceiver(int userId) {
        logger.info("Fetching transactions for receiver with ID: " + userId);
        List<Transactions> transactions = transactionRepository.findByReceiver(userId);
        if (transactions.isEmpty()) {
            logger.warn("Transactions not found for receiverID: " + userId);
            throw new NotFoundException("No transactions found for receiver with ID: " + userId);
        }
        return transactions;
    }

    public void addTransaction(Transactions transaction) {
        logger.info("Adding a new transaction for receiver ID: " + transaction.getReceiver());
        transaction.setTransactionDate(new Date());
        transactionRepository.save(transaction);
        logger.info("Transaction successfully saved.");
    }
    
    public Transactions getTransactionById(int transactionId) {
        logger.info("Fetching transaction with ID:"+ transactionId);
        
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    logger.error("Transaction not found with ID: "+ transactionId);
                    return new NotFoundException("Transaction not found with ID: " + transactionId);
                });
    }
}
