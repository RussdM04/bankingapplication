package com.example.banking.service;

import com.example.banking.model.*;
import com.example.banking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledPaymentService {

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BillerRepository billerRepository;

    /**
     * Runs every day at 1 AM to process due scheduled payments.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void processScheduledPayments() {
        List<ScheduledPayment> duePayments = scheduledPaymentRepository
                .findByScheduledDateAndStatus(LocalDate.now(), "PENDING");

        for (ScheduledPayment payment : duePayments) {
            try {
                Account account = payment.getFromAccount();
                BigDecimal amount = payment.getAmount();

                if (account.getBalance().compareTo(amount) >= 0) {
                    account.setBalance(account.getBalance().subtract(amount));
                    accountRepository.save(account);

                    Transaction transaction = new Transaction();
                    transaction.setAccount(account);
                    transaction.setTransactionType("SCHEDULED_PAYMENT");
                    transaction.setAmount(amount);
                    transaction.setBillerName(payment.getBiller().getBillerName());
                    transaction.setTimestamp(new java.util.Date());
                    transactionRepository.save(transaction);

                    payment.setStatus("COMPLETED");
                } else {
                    payment.setStatus("FAILED");
                }
            } catch (Exception e) {
                payment.setStatus("FAILED");
            }
            scheduledPaymentRepository.save(payment);
        }
    }

    /**
     * Retrieves all scheduled payments created by a specific user.
     */
    public List<ScheduledPayment> getScheduledPaymentsByUser(User user) {
        return scheduledPaymentRepository.findByUser(user);
    }

    /**
     * Creates a new scheduled payment for a user.
     */
    public void createScheduledPayment(User user, Long accountId, Long billerId, double amount, String scheduledDateStr) {
        Account fromAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid account ID"));

        Biller biller = billerRepository.findById(billerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid biller ID"));

        ScheduledPayment payment = new ScheduledPayment();
        payment.setUser(user);
        payment.setFromAccount(fromAccount);
        payment.setBiller(biller);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setScheduledDate(LocalDate.parse(scheduledDateStr));
        payment.setStatus("PENDING");

        scheduledPaymentRepository.save(payment);
    }
}