package com.rms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Date;
import com.rms.model.Transactions;

class TransactionsTest {

    @Test
    void testGettersAndSetters() {
        // Create an instance of Transactions
        Transactions transaction = new Transactions();
        Date date = new Date();
        
        // Set values using setters
        transaction.setTransactionId(1);
        transaction.setReceiver(1001);
        transaction.setSender(2001);
        transaction.setRoyaltyId(3001);
        transaction.setTransactionDate(date);
        transaction.setTransactionAmount(500.75);
        transaction.setManagerId(4001);
        transaction.setTransactionType("Payment");

        // Assert all positive and negative cases
        assertAll("Transaction Fields",
            () -> assertEquals(1, transaction.getTransactionId()),
            () -> assertNotEquals(2, transaction.getTransactionId()),
            
            () -> assertEquals(1001, transaction.getReceiver()),
            () -> assertNotEquals(999, transaction.getReceiver()),
            
            () -> assertEquals(2001, transaction.getSender()),
            () -> assertNotEquals(1999, transaction.getSender()),
            
            () -> assertEquals(3001, transaction.getRoyaltyId()),
            () -> assertNotEquals(3999, transaction.getRoyaltyId()),
            
            () -> assertEquals(date, transaction.getTransactionDate()),
            () -> assertNotEquals(new Date(), transaction.getTransactionDate()),
            
            () -> assertEquals(500.75, transaction.getTransactionAmount()),
            () -> assertNotEquals(999.99, transaction.getTransactionAmount()),
            
            () -> assertEquals(4001, transaction.getManagerId()),
            () -> assertNotEquals(4999, transaction.getManagerId()),
            
            () -> assertEquals("Payment", transaction.getTransactionType()),
            () -> assertNotEquals("Refund", transaction.getTransactionType())
        );
    }

    @Test
    void testConstructor() {
        Date date = new Date();
        Transactions transaction = new Transactions(1001, 2001, 3001, date, 500.75, 4001, "Payment");
        
        // Assert all positive and negative cases
        assertAll("Transaction Constructor",
            () -> assertEquals(1001, transaction.getReceiver()),
            () -> assertNotEquals(999, transaction.getReceiver()),
            
            () -> assertEquals(2001, transaction.getSender()),
            () -> assertNotEquals(1999, transaction.getSender()),
            
            () -> assertEquals(3001, transaction.getRoyaltyId()),
            () -> assertNotEquals(3999, transaction.getRoyaltyId()),
            
            () -> assertEquals(date, transaction.getTransactionDate()),
            () -> assertNotEquals(new Date(), transaction.getTransactionDate()),
            
            () -> assertEquals(500.75, transaction.getTransactionAmount()),
            () -> assertNotEquals(999.99, transaction.getTransactionAmount()),
            
            () -> assertEquals(4001, transaction.getManagerId()),
            () -> assertNotEquals(4999, transaction.getManagerId()),
            
            () -> assertEquals("Payment", transaction.getTransactionType()),
            () -> assertNotEquals("Refund", transaction.getTransactionType())
        );
    }

    @Test
    void testToString() {
        Date date = new Date();
        Transactions transaction = new Transactions(1001, 2001, 3001, date, 500.75, 4001, "Payment");
        
        // Construct the expected toString output
        String expectedToString = "Transactions(transactionId=0, receiver=1001, sender=2001, royaltyId=3001, " +
                                  "transactionDate=" + date + ", transactionAmount=500.75, managerId=4001, " +
                                  "transactionType=Payment)";
        
        // Assert positive and negative cases
        assertEquals(expectedToString, transaction.toString());
        assertNotEquals("Transactions(transactionId=999, receiver=999, sender=999, royaltyId=999, " +
                        "transactionDate=" + new Date() + ", transactionAmount=999.99, managerId=9999, " +
                        "transactionType=Refund)", transaction.toString());
    }
}
