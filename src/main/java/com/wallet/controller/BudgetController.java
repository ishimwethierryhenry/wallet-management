package com.wallet.controller;

import com.wallet.model.Budget;
import com.wallet.service.BudgetService;
import com.wallet.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;
    private final CategoryService categoryService;

    @GetMapping
    public String listBudgets(Model model) {
        model.addAttribute("budgets", budgetService.getActiveBudgets());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newBudget", new Budget());
        return "budgets/list";
    }

    @PostMapping
    public String createBudget(@ModelAttribute Budget budget) {
        budgetService.createBudget(budget);
        return "redirect:/budgets";
    }
}