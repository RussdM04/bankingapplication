package com.example.banking.controller;

import com.example.banking.model.*;
import com.example.banking.repository.BillerRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.*;
import com.example.banking.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BillerRepository billerRepository;

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpStorageService otpStorageService;

    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<Account> accounts = accountService.getAccountsByUser(user);
        List<Transaction> transactions = transactionRepository.findByAccountUser(user);
        List<Biller> billers = billerRepository.findAll();
        List<ScheduledPayment> scheduledPayments = scheduledPaymentService.getScheduledPaymentsByUser(user);
        double totalBalance = accounts.stream()
        .mapToDouble(account -> account.getBalance().doubleValue())
        .sum();
        double savingsBalance = accounts.stream()
            .filter(a -> a.getAccountType() == Account.AccountType.SAVINGS)
            .mapToDouble(a -> a.getBalance().doubleValue())
            .sum();

        double checkingBalance = accounts.stream()
            .filter(a -> a.getAccountType() == Account.AccountType.CHECKING)
            .mapToDouble(a -> a.getBalance().doubleValue())
            .sum();

        double businessBalance = accounts.stream()
            .filter(a -> a.getAccountType() == Account.AccountType.BUSINESS)
            .mapToDouble(a -> a.getBalance().doubleValue())
            .sum();

        
       
        model.addAttribute("accounts", accounts);
        model.addAttribute("totalBalance", totalBalance);
        model.addAttribute("savingsBalance", savingsBalance);
        model.addAttribute("checkingBalance", checkingBalance);
        model.addAttribute("businessBalance", businessBalance);
        model.addAttribute("transactions", transactions);
        model.addAttribute("billers", billers);
        model.addAttribute("scheduledPayments", scheduledPayments);
        model.addAttribute("accountTypes", Arrays.asList(Account.AccountType.values()));
        model.addAttribute("existingAccountTypes", accounts.stream().map(Account::getAccountType).toList());
        model.addAttribute("allAccounts", accountService.getAllAccounts());
        model.addAttribute("currentUser", user.getUsername());

        return "dashboard";
    }

    @PostMapping("/create")
    public String createAccount(@RequestParam String accountType, Authentication authentication, Model model) {
        try {
            Account.AccountType type = Account.AccountType.valueOf(accountType.toUpperCase());
            User user = userService.findByUsername(authentication.getName());
            Account newAccount = accountService.createAccount(user, type);
            if (newAccount == null) {
                model.addAttribute("errorMessage", "You already have a " + type + " account.");
                return showDashboard(authentication, model);
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid account type selected.");
            return showDashboard(authentication, model);
        }
        return "redirect:/account/dashboard";
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestParam Long accountId, @RequestParam double amount, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        accountService.addMoney(user, accountId, amount);
        return "redirect:/account/dashboard";
    }

    @PostMapping("/withdraw-money")
    public String withdrawMoney(@RequestParam Long accountId, @RequestParam double amount, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        try {
            accountService.withdrawMoney(user, accountId, amount);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return showDashboard(authentication, model);
        }
        return "redirect:/account/dashboard";
    }

    @PostMapping("/initiate-transfer")
    public String initiateTransfer(@RequestParam String fromAccount,
                                   @RequestParam String toAccount,
                                   @RequestParam double amount,
                                   Authentication authentication,
                                   Model model) {

        User user = userService.findByUsername(authentication.getName());

        if (amount >= 500) {//use more than 500 to test otp is working or not//
            String otp = OtpUtil.generateOtp();
            otpStorageService.saveOtp(user.getUsername(), otp);
            emailService.sendOtpEmail(user.getEmail(), otp);

            model.addAttribute("fromAccount", fromAccount);
            model.addAttribute("toAccount", toAccount);
            model.addAttribute("amount", amount);
            return "verify-otp";
        }

        accountService.transferFunds(user, fromAccount, toAccount, amount);
        return "redirect:/account/dashboard";
    }

    @PostMapping("/verify-transfer-otp")
    public String verifyTransferOtp(@RequestParam String fromAccount,
                                    @RequestParam String toAccount,
                                    @RequestParam double amount,
                                    @RequestParam String otp,
                                    Authentication authentication,
                                    Model model) {

        User user = userService.findByUsername(authentication.getName());
        String storedOtp = otpStorageService.getOtp(user.getUsername());

        if (storedOtp != null && storedOtp.equals(otp)) {
            accountService.transferFunds(user, fromAccount, toAccount, amount);
            otpStorageService.clearOtp(user.getUsername());
            return "redirect:/account/dashboard";
        } else {
            model.addAttribute("errorMessage", "Invalid OTP. Please try again.");
            model.addAttribute("fromAccount", fromAccount);
            model.addAttribute("toAccount", toAccount);
            model.addAttribute("amount", amount);
            return "verify-otp";
        }
    }

    @PostMapping("/pay-bill")
    public String payBill(@RequestParam String fromAccount, @RequestParam String billerName,
                          @RequestParam double amount, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        try {
            accountService.payBill(user, fromAccount, billerName, amount);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return showDashboard(authentication, model);
        }
        return "redirect:/account/dashboard";
    }

    @GetMapping("/scheduled-payment/create")
    public String showCreateScheduledPaymentForm(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        List<Account> accounts = accountService.getAccountsByUser(user);
        List<Biller> billers = billerRepository.findAll();

        model.addAttribute("accounts", accounts);
        model.addAttribute("billers", billers);
        return "scheduled-payment/create";
    }
    
    @PostMapping("/scheduled-payment/create")
    public String createScheduledPayment(@RequestParam Long accountId,
                                         @RequestParam Long billerId,
                                         @RequestParam double amount,
                                         @RequestParam String schedule,
                                         Authentication authentication,
                                         Model model) {
        User user = userService.findByUsername(authentication.getName());
        try {
            scheduledPaymentService.createScheduledPayment(user, accountId, billerId, amount, schedule);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return showCreateScheduledPaymentForm(authentication, model);
        }
        return "redirect:/account/dashboard";
    }
}