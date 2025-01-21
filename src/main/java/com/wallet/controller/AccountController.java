package com.wallet.controller;

import com.wallet.model.Account;
import com.wallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public String listAccounts(Model model, HttpServletRequest request) {
        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("newAccount", new Account());
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("accountTypes", Account.AccountType.values()); // Add if using an enum for account types
        return "accounts/list";
    }

    @PostMapping
    public String createAccount(@ModelAttribute Account account) {
        accountService.createAccount(account);
        return "redirect:/accounts";
    }

//    @PostMapping("/accounts")
//    public String createAccount(@ModelAttribute("newAccount") Account newAccount) {
//        accountService.createAccount(newAccount);
//        return "redirect:/accounts"; // Redirect to the accounts list after creation
//    }
}