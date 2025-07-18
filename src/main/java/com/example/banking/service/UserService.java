package com.example.banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.banking.model.User;
import com.example.banking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Service
 * Handles business logic for user-related operations including:
 * - User registration and validation
 * - Password encryption
 * - Database operations through UserRepository
 * - Logging of user-related activities
 */
@Service
public class UserService {

    /** Logger for tracking service operations */
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /** Repository for user data persistence operations */
    @Autowired
    private UserRepository userRepository;

    /** Encoder for secure password hashing */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system
     * This method:
     * 1. Validates that email and username are unique
     * 2. Encrypts the user's password
     * 3. Saves the user to the database
     * 4. Logs the registration process
     *
     * @param user the User object containing registration data
     * @return the saved User object with encrypted password
     * @throws RuntimeException if email is already registered or username is taken
     */
    public User registerUser(User user) {
        logger.info("Attempting to register user: {}", user.getUsername());

        // Validate email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("Registration failed: Email {} already exists", user.getEmail());
            throw new RuntimeException("Email already registered");
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Registration failed: Username {} already exists", user.getUsername());
            throw new RuntimeException("Username already taken");
        }

        // Encrypt password and save user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    /**
     * Finds a user by their username
     * Used for retrieving user details during authentication and account operations
     *
     * @param username the username to search for
     * @return the User object if found
     * @throws RuntimeException if no user is found with the given username
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }
} 