package com.example.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Long id, String description, BigDecimal amount, String category, LocalDate date) {}
