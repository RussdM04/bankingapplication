package com.example.banking.repository;

import com.example.banking.model.ScheduledPayment;
import com.example.banking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {

    // Get all scheduled payments due today
    List<ScheduledPayment> findByScheduledDateAndStatus(LocalDate scheduledDate, String status);

    // Get all scheduled payments created by a specific user
    List<ScheduledPayment> findByUser(User user);
}