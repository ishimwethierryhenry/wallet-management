package com.wallet.dto;

import com.wallet.model.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long accountId;
    private String accountName;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private Transaction.TransactionType type;
    private LocalDateTime dateTime;
    private String description;

    // Constructor from Entity
    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.accountId = transaction.getAccount().getId();
        this.accountName = transaction.getAccount().getName();
        if (transaction.getCategory() != null) {
            this.categoryId = transaction.getCategory().getId();
            this.categoryName = transaction.getCategory().getName();
        }
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.dateTime = transaction.getDateTime();
        this.description = transaction.getDescription();
    }

    // Convert DTO to Entity
    public Transaction toEntity() {
        Transaction transaction = new Transaction();
        transaction.setId(this.id);
        // Note: Account and Category need to be set separately using their respective services
        transaction.setAmount(this.amount);
        transaction.setType(this.type);
        transaction.setDateTime(this.dateTime);
        transaction.setDescription(this.description);
        return transaction;
    }

    // Additional helper methods
    public boolean isExpense() {
        return Transaction.TransactionType.EXPENSE.equals(this.type);
    }

    public boolean isIncome() {
        return Transaction.TransactionType.INCOME.equals(this.type);
    }

    public String getFormattedAmount() {
        return (isExpense() ? "-" : "+") + amount.toString();
    }

    public String getFormattedDateTime() {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}