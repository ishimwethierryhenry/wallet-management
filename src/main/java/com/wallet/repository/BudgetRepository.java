package com.wallet.repository;

import com.wallet.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByEndDateGreaterThanEqualAndStartDateLessThanEqual(LocalDate date, LocalDate date2);
    List<Budget> findByCategoryId(Long categoryId);
}