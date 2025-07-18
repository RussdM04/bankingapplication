package com.example.banking.repository;

import com.example.banking.model.Transaction;
import com.example.banking.model.Account;
import com.example.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a specific account
    List<Transaction> findByAccount(Account account);

    // Get all transactions for a user based on their accounts
    List<Transaction> findByAccountUser(User user);
}