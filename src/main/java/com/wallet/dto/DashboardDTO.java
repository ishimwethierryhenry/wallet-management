package com.wallet.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDTO {
    private List<AccountDTO> accounts;
    private List<TransactionDTO> recentTransactions;
    private List<BudgetDTO> activeBudgets;

    // Chart Data
    private List<String> chartLabels;
    private List<BigDecimal> incomeData;
    private List<BigDecimal> expenseData;

    // Category Chart Data
    private List<String> categoryLabels;
    private List<BigDecimal> categoryData;

    // Summary Statistics
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;

    @Data
    public static class ChartData {
        private List<String> labels;
        private Map<String, List<BigDecimal>> datasets;
    }

    @Data
    public static class AccountDTO {
        private Long id;
        private String name;
        private BigDecimal balance;
        private String accountType;
    }

    @Data
    public static class BudgetDTO {
        private Long id;
        private String category;
        private BigDecimal limit;
        private BigDecimal spent;
        private BigDecimal remaining;
        private double percentageUsed;
    }
}