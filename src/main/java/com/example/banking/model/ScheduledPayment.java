package com.example.banking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private LocalDate scheduledDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Account fromAccount;

    @ManyToOne
    private Biller biller;

    private String status;  // Needed for setStatus()

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public User getUser() {
        return user;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Biller getBiller() {
        return biller;
    }

    public String getStatus() {
        return status;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setBiller(Biller biller) {
        this.biller = biller;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}