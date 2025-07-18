package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Biller;
import com.example.banking.model.ScheduledPayment;
import com.example.banking.model.Transaction;
import com.example.banking.model.User;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.BillerRepository;
import com.example.banking.repository.ScheduledPaymentRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    private EmailService emailService;

    public Account createAccount(User user, Account.AccountType accountType) {
        if (accountRepository.existsByUserAndAccountType(user, accountType)) {
            return null;
        }

        String accountNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Account account = new Account();
        account.setUser(user);
        account.setAccountType(accountType);
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);

        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account addMoney(User user, Long accountId, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");
        Account account = getAccountByIdAndUser(accountId, user);
        account.addBalance(BigDecimal.valueOf(amount));
        logTransaction(account, "DEPOSIT", amount, null, null);
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdrawMoney(User user, Long accountId, double amount ) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");
        Account account = getAccountByIdAndUser(accountId, user);
        account.deductBalance(BigDecimal.valueOf(amount));
        logTransaction(account, "WITHDRAWAL", amount, null, null);
        double FRAUD_THRESHOLD = 10000;//use more than 10000 transtion to check fraud notification working
        if (amount >= FRAUD_THRESHOLD) {
            emailService.sendFraudAlert(
                user.getEmail(),
                amount,
                account.getAccountType().toString(),
                account.getAccountNumber()
            );
             
        }
        return accountRepository.save(account);
    }

    @Transactional
    public boolean transferFunds(User user, String fromAccountNumber, String toAccountNumber, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .filter(acc -> acc.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("Invalid source account."));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found."));

        BigDecimal amt = BigDecimal.valueOf(amount);

        if (fromAccount.getBalance().compareTo(amt) < 0) {
            throw new IllegalStateException("Insufficient balance for transfer.");
        }
        
        fromAccount.deductBalance(amt);
        toAccount.addBalance(amt);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        logTransaction(fromAccount, "TRANSFER", amount, toAccountNumber, null);
        logTransaction(toAccount, "TRANSFER", amount, fromAccountNumber, null);

        return true;
    }

    @Transactional
    public boolean payBill(User user, String fromAccountNumber, String billerName, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");

        Account account = accountRepository.findByAccountNumber(fromAccountNumber)
                .filter(acc -> acc.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("Account not found or access denied."));

        Biller biller = billerRepository.findByBillerName(billerName)
                .orElseThrow(() -> new IllegalArgumentException("Biller not found."));

        BigDecimal amt = BigDecimal.valueOf(amount);

        if (account.getBalance().compareTo(amt) < 0) {
            throw new IllegalStateException("Insufficient balance for bill payment.");
        }

        account.deductBalance(amt);
        accountRepository.save(account);

        logTransaction(account, "BILL_PAYMENT", amount, biller.getBillerAccountNumber(), billerName);

        return true;
    }

    public void schedulePayment(User user, String fromAccountNumber, String billerName, double amount, String scheduledDateStr) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be greater than 0.");

        Account account = accountRepository.findByAccountNumber(fromAccountNumber)
                .filter(acc -> acc.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("Invalid account."));

        Biller biller = billerRepository.findByBillerName(billerName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid biller."));

        LocalDate scheduledDate = LocalDate.parse(scheduledDateStr);

        ScheduledPayment payment = new ScheduledPayment();
        payment.setUser(user);
        payment.setFromAccount(account);
        payment.setBiller(biller);
        payment.setAmount(BigDecimal.valueOf(amount));
        payment.setScheduledDate(scheduledDate);

        scheduledPaymentRepository.save(payment);
    }

    private Account getAccountByIdAndUser(Long accountId, User user) {
        return accountRepository.findById(accountId)
                .filter(account -> account.getUser().equals(user))
                .orElseThrow(() -> new IllegalStateException("Account not found or does not belong to the user."));
    }

    private void logTransaction(Account account, String type, double amount, String recipientAccount, String billerName) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setTransactionType(type);
        tx.setAmount(BigDecimal.valueOf(amount));
        tx.setRecipientAccount(recipientAccount);
        tx.setBillerName(billerName);
        tx.setTimestamp(new Date());
        transactionRepository.save(tx);
    }
}