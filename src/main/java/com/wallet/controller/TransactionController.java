package com.wallet.controller;

import com.wallet.model.Transaction;
import com.wallet.service.TransactionService;
import com.wallet.service.AccountService;
import com.wallet.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @GetMapping
    public String listTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newTransaction", new Transaction());
        return "transactions/list";
    }

//    @PostMapping
//    public String createTransaction(@ModelAttribute Transaction transaction) {
//        transactionService.createTransaction(transaction);
//        return "redirect:/transactions";
//    }

    @PostMapping
    public String createTransaction(@ModelAttribute Transaction transaction) {
        // Ensure that the dateTime is set before saving
        transaction.setDateTime(LocalDateTime.now()); // Or use the value from the form
        transactionService.createTransaction(transaction);
        return "redirect:/transactions";
    }
}