package com.example.banking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Transaction Entity
 * Represents financial transactions in the system.
 */
@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER, BILL_PAYMENT

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "recipient_account")
    private String recipientAccount; // For transfers (nullable)

    @Column(name = "biller_name")
    private String billerName; // For bill payments (nullable)

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp = new Date(); // Default to current time
}