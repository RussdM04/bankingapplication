package com.example.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.example.banking.model.User;
import com.example.banking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication Controller
 * Handles all authentication-related requests including login and registration.
 * This controller manages user authentication flows and form submissions.
 */
@Controller
public class AuthController {

    /** Logger for this class */
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /** Service for handling user-related operations */
    @Autowired
    private UserService userService;

    /**
     * Displays the login page
     * Spring Security handles the actual login processing
     * @return the login view template name
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Displays the registration page
     * @param model Spring MVC model to pass data to the view
     * @return the register view template name
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * Processes user registration
     * Handles the registration form submission, validates user input,
     * and creates a new user account if validation passes
     *
     * @param user the User object populated from form data
     * @param result binding result for validation errors
     * @param model Spring MVC model to pass data to the view
     * @return redirect to login page on success, or back to register page on error
     */
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             Model model) {
        logger.info("Received registration request for user: {}", user.getUsername());
        
        // Check for validation errors
        if (result.hasErrors()) {
            logger.error("Validation errors: {}", result.getAllErrors());
            return "register";
        }

        try {
            // Attempt to register the user
            User registeredUser = userService.registerUser(user);
            logger.info("User registered successfully: {}", registeredUser.getUsername());
            return "redirect:/login?success=Registration successful! Please login.";
        } catch (RuntimeException e) {
            // Handle registration failures (e.g., duplicate email)
            logger.error("Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
} 