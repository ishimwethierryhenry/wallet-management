package com.wallet.service;

import com.wallet.model.Transaction;
import com.wallet.model.Account;
import com.wallet.repository.TransactionRepository;
import com.wallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Account account = accountRepository.findById(transaction.getAccount().getId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Update account balance
        if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }

    public Map<String, Object> getChartData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = getTransactionsByDateRange(startDate, endDate);

        // Process data for charts
        Map<String, Object> chartData = new HashMap<>();

        // Income vs Expenses by month
        Map<String, BigDecimal> monthlyIncome = new LinkedHashMap<>();
        Map<String, BigDecimal> monthlyExpenses = new LinkedHashMap<>();

        // Category distribution
        HashMap<String, BigDecimal> categoryTotals = new HashMap<>(); // Change Object to String and BigDecimal

        for (Transaction t : transactions) {
            String monthYear = t.getDateTime().format(DateTimeFormatter.ofPattern("MMM yyyy"));

            if (t.getType() == Transaction.TransactionType.INCOME) {
                monthlyIncome.merge(monthYear, t.getAmount(), BigDecimal::add);
            } else {
                monthlyExpenses.merge(monthYear, t.getAmount(), BigDecimal::add);
                if (t.getCategory() != null) {
                    categoryTotals.merge(t.getCategory().getName(), t.getAmount(), BigDecimal::add);
                }
            }
        }

        chartData.put("chartLabels", new ArrayList<>(monthlyIncome.keySet()));
        chartData.put("incomeData", new ArrayList<>(monthlyIncome.values()));
        chartData.put("expenseData", new ArrayList<>(monthlyExpenses.values()));
        chartData.put("categoryLabels", new ArrayList<>(categoryTotals.keySet())); // Fix the key type
        chartData.put("categoryData", new ArrayList<>(categoryTotals.values())); // Fix the value type

        return chartData;
    }
}