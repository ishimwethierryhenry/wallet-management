package com.wallet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Column(length = 255) // Optional: specify length if needed
    private String description;

    // Add these fields
    private BigDecimal monthlyIncome; // Add this line
    private BigDecimal monthlyExpenses; // Add this line

    public enum AccountType {
        BANK_ACCOUNT,
        MOBILE_MONEY,
        CASH,
        OTHER
    }
}