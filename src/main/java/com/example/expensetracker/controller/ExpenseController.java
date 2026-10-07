package com.example.expensetracker.controller;

import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(Authentication auth, @Valid @RequestBody ExpenseRequest req) {
        return service.create(auth.getName(), req);
    }

    @GetMapping
    public List<ExpenseResponse> list(Authentication auth, @RequestParam(required = false) String category) {
        return service.list(auth.getName(), category);
    }

    @GetMapping("/summary")
    public Map<String, BigDecimal> summary(Authentication auth) {
        return service.summary(auth.getName());
    }

    @GetMapping("/{id}")
    public ExpenseResponse get(Authentication auth, @PathVariable Long id) {
        return service.get(auth.getName(), id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(Authentication auth, @PathVariable Long id,
                                  @Valid @RequestBody ExpenseRequest req) {
        return service.update(auth.getName(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication auth, @PathVariable Long id) {
        service.delete(auth.getName(), id);
    }
}
