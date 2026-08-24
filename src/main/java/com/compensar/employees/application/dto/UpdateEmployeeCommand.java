package com.compensar.employees.application.dto;

import com.compensar.employees.domain.model.EmployeeStatus;

import java.time.LocalDate;

public record UpdateEmployeeCommand(
        String firstName,
        String lastName,
        String documentId,
        String email,
        String position,
        LocalDate hireDate,
        EmployeeStatus status) {
}
