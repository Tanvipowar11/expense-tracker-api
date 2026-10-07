package com.example.expensetracker.service;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExpenseService {
    private final ExpenseRepository expenses;
    private final UserRepository users;

    public ExpenseService(ExpenseRepository expenses, UserRepository users) {
        this.expenses = expenses;
        this.users = users;
    }

    public ExpenseResponse create(String username, ExpenseRequest req) {
        User owner = users.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"));
        Expense e = new Expense();
        e.setOwner(owner);
        apply(e, req);
        return toResponse(expenses.save(e));
    }

    public List<ExpenseResponse> list(String username, String category) {
        List<Expense> found = (category == null || category.isBlank())
                ? expenses.findByOwnerUsername(username)
                : expenses.findByOwnerUsernameAndCategoryIgnoreCase(username, category);
        return found.stream().map(this::toResponse).toList();
    }

    public ExpenseResponse get(String username, Long id) {
        return toResponse(find(username, id));
    }

    public ExpenseResponse update(String username, Long id, ExpenseRequest req) {
        Expense e = find(username, id);
        apply(e, req);
        return toResponse(expenses.save(e));
    }

    public void delete(String username, Long id) {
        expenses.delete(find(username, id));
    }

    /** Total spend per category for the user. */
    public Map<String, BigDecimal> summary(String username) {
        Map<String, BigDecimal> totals = new TreeMap<>();
        for (Expense e : expenses.findByOwnerUsername(username)) {
            totals.merge(e.getCategory(), e.getAmount(), BigDecimal::add);
        }
        return totals;
    }

    private Expense find(String username, Long id) {
        return expenses.findByIdAndOwnerUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    private void apply(Expense e, ExpenseRequest req) {
        e.setDescription(req.description());
        e.setAmount(req.amount());
        e.setCategory(req.category());
        e.setDate(req.date());
    }

    private ExpenseResponse toResponse(Expense e) {
        return new ExpenseResponse(e.getId(), e.getDescription(), e.getAmount(), e.getCategory(), e.getDate());
    }
}
