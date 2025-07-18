package com.example.banking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account Entity
 * Represents a user's bank account in the system.
 */
@Entity
@Table(name = "accounts")
@Data
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne  // Allow multiple accounts per user
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String accountNumber;  // Unique account number (generated in AccountService)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enum for Account Types
    public enum AccountType {
        SAVINGS,
        CHECKING,
        BUSINESS
    }

    // Method to check if an account has sufficient balance
    public boolean hasSufficientBalance(BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    // Deduct balance (for withdrawals, transfers, bill payments)
    public void deductBalance(BigDecimal amount) {
        if (!hasSufficientBalance(amount)) {
            throw new IllegalStateException("Insufficient funds in " + this.accountType + " account.");
        }
        this.balance = this.balance.subtract(amount);
    }

    // Add balance (for deposits, receiving transfers)
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}