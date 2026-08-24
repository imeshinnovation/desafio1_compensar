package com.compensar.employees.presentation.dto;

import com.compensar.employees.domain.model.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateEmployeeRequest(
        @NotBlank(message = "firstName is required")
        @Size(max = 100, message = "firstName must not exceed 100 characters")
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 100, message = "lastName must not exceed 100 characters")
        String lastName,

        @NotBlank(message = "documentId is required")
        @Size(max = 20, message = "documentId must not exceed 20 characters")
        String documentId,

        @NotBlank(message = "email is required")
        @Email(message = "email must have a valid format")
        @Size(max = 100, message = "email must not exceed 100 characters")
        String email,

        @Size(max = 100, message = "position must not exceed 100 characters")
        String position,

        @NotNull(message = "hireDate is required")
        LocalDate hireDate,

        EmployeeStatus status) {
}
