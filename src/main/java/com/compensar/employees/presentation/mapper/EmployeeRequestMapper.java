package com.compensar.employees.presentation.mapper;

import com.compensar.employees.application.dto.CreateEmployeeCommand;
import com.compensar.employees.application.dto.UpdateEmployeeCommand;
import com.compensar.employees.presentation.dto.CreateEmployeeRequest;
import com.compensar.employees.presentation.dto.UpdateEmployeeRequest;

/**
 * Mapeo de peticiones HTTP de empleados a comandos de la capa de aplicación.
 */
public final class EmployeeRequestMapper {

    private EmployeeRequestMapper() {
    }

    public static CreateEmployeeCommand toCreateCommand(CreateEmployeeRequest request) {
        return new CreateEmployeeCommand(
                request.firstName(),
                request.lastName(),
                request.documentId(),
                request.email(),
                request.position(),
                request.hireDate(),
                request.status());
    }

    public static UpdateEmployeeCommand toUpdateCommand(UpdateEmployeeRequest request) {
        return new UpdateEmployeeCommand(
                request.firstName(),
                request.lastName(),
                request.documentId(),
                request.email(),
                request.position(),
                request.hireDate(),
                request.status());
    }
}
