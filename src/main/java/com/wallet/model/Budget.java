package com.wallet.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal spent;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne // Add this line to establish a relationship with User
    @JoinColumn(name = "user_id", nullable = false) // Ensure this column exists in your database
    private User user; // Add this field

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean notificationSent;
}