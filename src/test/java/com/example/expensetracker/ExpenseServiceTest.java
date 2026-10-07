package com.example.expensetracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import com.example.expensetracker.service.ExpenseService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ExpenseServiceTest {

    private Expense expense(String category, String amount) {
        Expense e = new Expense();
        e.setCategory(category);
        e.setAmount(new BigDecimal(amount));
        return e;
    }

    @Test
    void summaryTotalsAmountsPerCategory() {
        ExpenseRepository repo = mock(ExpenseRepository.class);
        UserRepository users = mock(UserRepository.class);
        when(repo.findByOwnerUsername("alice")).thenReturn(List.of(
                expense("Food", "10.50"), expense("Food", "4.50"), expense("Travel", "30.00")));

        Map<String, BigDecimal> summary = new ExpenseService(repo, users).summary("alice");

        assertEquals(0, new BigDecimal("15.00").compareTo(summary.get("Food")));
        assertEquals(0, new BigDecimal("30.00").compareTo(summary.get("Travel")));
        assertEquals(2, summary.size());
    }
}
