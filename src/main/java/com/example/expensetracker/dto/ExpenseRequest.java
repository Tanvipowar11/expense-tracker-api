package com.example.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank @Size(max = 100) String description,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotBlank @Size(max = 50) String category,
        @NotNull @PastOrPresent LocalDate date) {}
