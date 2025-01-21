package com.wallet.service;

import com.wallet.model.Budget;
import com.wallet.model.User;
import com.wallet.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final NotificationService notificationService;
    private final JavaMailSender emailSender;
    private final BudgetRepository budgetRepository;

    public List<Budget> getActiveBudgets() {
        LocalDate now = LocalDate.now();
        return budgetRepository.findByEndDateGreaterThanEqualAndStartDateLessThanEqual(now, now);
    }

    public Budget createBudget(Budget budget) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser  = (User) authentication.getPrincipal(); // Assuming UserDetails is your User class

        // Set the user to the budget
        budget.setUser (currentUser );

        budget.setSpent(BigDecimal.ZERO);
        budget.setNotificationSent(false);
        return budgetRepository.save(budget);
    }

    public void updateBudgetSpending(Long budgetId, BigDecimal amount) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budget.setSpent(budget.getSpent().add(amount));

        // Check if budget exceeded
        if (budget.getSpent().compareTo(budget.getAmount()) > 0 && !budget.isNotificationSent()) {
            budget.setNotificationSent(true);
            // TODO: Send notification
        }

        budgetRepository.save(budget);
    }

    @Async
    public void checkBudgetAndNotify(Budget budget) {
        if (budget.getSpent().compareTo(budget.getAmount()) > 0 && !budget.isNotificationSent()) {
            String message = String.format(
                    "Budget alert: Your budget '%s' (%.2f) has been exceeded. Current spending: %.2f",
                    budget.getName(),
                    budget.getAmount(),
                    budget.getSpent()
            );

            // Send email notification
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(budget.getUser().getEmail());
            email.setSubject("Budget Exceeded Alert");
            email.setText(message);
            emailSender.send(email);

            // Update notification status
            budget.setNotificationSent(true);
            budgetRepository.save(budget);
        }
    }
}
