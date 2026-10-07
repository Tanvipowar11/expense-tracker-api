package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByOwnerUsername(String username);
    List<Expense> findByOwnerUsernameAndCategoryIgnoreCase(String username, String category);
    Optional<Expense> findByIdAndOwnerUsername(Long id, String username);
}
