package com.example.banking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * User Entity
 * Represents a user in the system with their authentication and profile information.
 * This class is mapped to the 'users' table in the database and includes
 * validation constraints for data integrity.
 */
@Entity
@Table(name = "users")
@Data  // Lombok annotation to generate getters, setters, equals, hashCode, and toString
public class User {

    /**
     * Unique identifier for the user
     * Auto-generated using database identity strategy
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's username
     * Must be unique and between 3-50 characters
     * Used for display and identification purposes
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true)
    private String username;

    /**
     * User's email address
     * Must be unique and in valid email format
     * Used for authentication and communication
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    /**
     * User's password
     * Must be at least 6 characters long
     * Stored in encrypted format (handled by Spring Security)
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    /**
     * User's age
     * Must be between 18 and 120 years old
     */
    @Min(value = 18, message = "Age must be between 18 and 120")
    private int age;

    /**
     * User's phone number
     * Must match a valid phone number format (10 to 15 digits)
     */
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phone;

    /**
     * User's gender
     * Must be one of the predefined options: Male, Female, Other
     */
    @NotBlank(message = "Gender is required")
    private String gender;
}