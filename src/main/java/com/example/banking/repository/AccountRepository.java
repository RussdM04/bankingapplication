package com.example.banking.repository;

import com.example.banking.model.Account;
import com.example.banking.model.User;
import com.example.banking.model.Account.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUser(User user);

    Optional<Account> findByUserAndAccountType(User user, AccountType accountType);

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByUserAndAccountType(User user, AccountType accountType);
}