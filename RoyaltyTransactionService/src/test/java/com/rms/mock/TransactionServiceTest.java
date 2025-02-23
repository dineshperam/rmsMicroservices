package com.rms.mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.rms.exeptions.NotFoundException;
import com.rms.model.Transactions;
import com.rms.repository.TransactionRepository;
import com.rms.service.TransactionService;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowTrans_Success() {
        // Arrange
        Transactions transaction1 = new Transactions();
        transaction1.setTransactionId(1);
        Transactions transaction2 = new Transactions();
        transaction2.setTransactionId(2);
        List<Transactions> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findAll()).thenReturn(transactions);

        // Act
        List<Transactions> result = transactionService.showTrans();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getTransactionId());
        assertEquals(2, result.get(1).getTransactionId());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testShowTrans_NoTransactionsFound() {
        // Arrange
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> transactionService.showTrans());
        assertEquals("No transactions found.", exception.getMessage());
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testShowTransByReceiver_Success() {
        // Arrange
        Transactions transaction1 = new Transactions();
        transaction1.setReceiver(1);
        Transactions transaction2 = new Transactions();
        transaction2.setReceiver(1);
        List<Transactions> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findByReceiver(1)).thenReturn(transactions);

        // Act
        List<Transactions> result = transactionService.showTransByReceiver(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getReceiver());
        assertEquals(1, result.get(1).getReceiver());
        verify(transactionRepository, times(1)).findByReceiver(1);
    }

    @Test
    void testShowTransByReceiver_NoTransactionsFound() {
        // Arrange
        when(transactionRepository.findByReceiver(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> transactionService.showTransByReceiver(1));
        assertEquals("No transactions found for user with ID: 1", exception.getMessage());
        verify(transactionRepository, times(1)).findByReceiver(1);
    }

    @Test
    void testShowTransByManId_Success() {
        // Arrange
        Transactions transaction1 = new Transactions();
        transaction1.setManagerId(1);
        Transactions transaction2 = new Transactions();
        transaction2.setManagerId(1);
        List<Transactions> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findByManagerId(1)).thenReturn(transactions);

        // Act
        List<Transactions> result = transactionService.showTransByManId(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getManagerId());
        assertEquals(1, result.get(1).getManagerId());
        verify(transactionRepository, times(1)).findByManagerId(1);
    }

    @Test
    void testShowTransByManId_NoTransactionsFound() {
        // Arrange
        when(transactionRepository.findByManagerId(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> transactionService.showTransByManId(1));
        assertEquals("No transactions found for manager with ID: 1", exception.getMessage());
        verify(transactionRepository, times(1)).findByManagerId(1);
    }

    @Test
    void testGetTransactionsByReceiver_Success() {
        // Arrange
        Transactions transaction1 = new Transactions();
        transaction1.setReceiver(1);
        Transactions transaction2 = new Transactions();
        transaction2.setReceiver(1);
        List<Transactions> transactions = Arrays.asList(transaction1, transaction2);
        
        when(transactionRepository.findByReceiver(1)).thenReturn(transactions);

        // Act
        List<Transactions> result = transactionService.getTransactionsByReceiver(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getReceiver());
        assertEquals(1, result.get(1).getReceiver());
        verify(transactionRepository, times(1)).findByReceiver(1);
    }

    @Test
    void testGetTransactionsByReceiver_NoTransactionsFound() {
        // Arrange
        when(transactionRepository.findByReceiver(1)).thenReturn(Collections.emptyList());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> transactionService.getTransactionsByReceiver(1));
        assertEquals("No transactions found for receiver with ID: 1", exception.getMessage());
        verify(transactionRepository, times(1)).findByReceiver(1);
    }

    @Test
    void testAddTransaction_Success() {
        // Arrange
        Transactions transaction = new Transactions();
        transaction.setReceiver(1);
        transaction.setTransactionAmount(100);
        
        when(transactionRepository.save(any(Transactions.class))).thenReturn(transaction);

        // Act
        transactionService.addTransaction(transaction);

        // Assert
        assertNotNull(transaction.getTransactionDate());
        verify(transactionRepository, times(1)).save(transaction);
        verify(transactionRepository).save(argThat(savedTransaction -> 
        savedTransaction.getTransactionDate() != null &&
        savedTransaction.getReceiver() == 1 &&
        Math.abs(savedTransaction.getTransactionAmount() - 100.0) < 0.0001
    ));
    }

    @Test
    void testAddTransaction_Exception() {
        // Arrange
        Transactions transaction = new Transactions();
        when(transactionRepository.save(any(Transactions.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> transactionService.addTransaction(transaction));
        verify(transactionRepository, times(1)).save(transaction);
    }
    
    @Test
    void testGetTransactionById_Success() {
        // Arrange
        int transactionId = 1;
        Transactions mockTransaction = new Transactions();
        mockTransaction.setTransactionId(transactionId);
        
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(mockTransaction));

        // Act
        Transactions result = transactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testGetTransactionById_NotFound() {
        // Arrange
        int transactionId = 999;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> transactionService.getTransactionById(transactionId));

        assertEquals("Transaction not found with ID: " + transactionId, exception.getMessage());
        verify(transactionRepository, times(1)).findById(transactionId);
    }
}
