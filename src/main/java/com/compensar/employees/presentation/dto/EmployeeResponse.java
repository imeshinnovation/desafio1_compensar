package com.compensar.employees.presentation.dto;

import com.compensar.employees.domain.model.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String documentId,
        String email,
        String position,
        LocalDate hireDate,
        EmployeeStatus status) {
}
