package com.compensar.employees.presentation.mapper;

import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.presentation.dto.EmployeeResponse;

/**
 * Mapeo del modelo de dominio de empleado al DTO expuesto por la API.
 */
public final class EmployeeResponseMapper {

    private EmployeeResponseMapper() {
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDocumentId(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getStatus());
    }
}
