package com.example.banking.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Biller Entity
 * Represents utility service providers.
 */
@Entity
@Table(name = "billers")
@Data
public class Biller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String billerName;

    @Column(nullable = false, unique = true)
    private String billerAccountNumber;
}