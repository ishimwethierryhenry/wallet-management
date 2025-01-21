package com.wallet.controller;
import com.wallet.service.ReportService;
import com.wallet.service.AccountService;
import com.wallet.service.CategoryService;
import com.wallet.service.TransactionService;
import com.wallet.service.BudgetService;
import com.wallet.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class DashboardController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final CategoryService categoryService;
    private final ReportService reportService;

    @GetMapping
    public String dashboard(Model model) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);
        LocalDateTime endDate = LocalDateTime.now();

        model.addAttribute("accounts", accountService.getAllAccounts());
        model.addAttribute("recentTransactions",
                transactionService.getTransactionsByDateRange(
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                )
        );
        model.addAttribute("activeBudgets", budgetService.getActiveBudgets());

        // Add chart data
        Map<String, Object> chartData = transactionService.getChartData(startDate, endDate);
        model.addAllAttributes(chartData);

        return "dashboard/index";
    }

    private void prepareChartData(Model model, List<Transaction> transactions) {
        // Prepare data for income vs expenses chart
        Map<String, BigDecimal> incomeByDay = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        t -> t.getDateTime().toLocalDate().toString(),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        Map<String, BigDecimal> expensesByDay = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getDateTime().toLocalDate().toString(),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        model.addAttribute("chartLabels", incomeByDay.keySet());
        model.addAttribute("incomeData", incomeByDay.values());
        model.addAttribute("expenseData", expensesByDay.values());

        // Prepare data for category distribution chart
        Map<String, BigDecimal> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE && t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        model.addAttribute("categoryLabels", expensesByCategory.keySet());
        model.addAttribute("categoryData", expensesByCategory.values());
    }

    @GetMapping("/api/dashboard/chart-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChartData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getChartData(startDate, endDate));
    }
}