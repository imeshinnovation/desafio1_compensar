package com.compensar.employees.application.usecase;

import com.compensar.employees.application.dto.CreateEmployeeCommand;
import com.compensar.employees.domain.exception.DuplicateEmployeeException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.model.EmployeeStatus;
import com.compensar.employees.domain.repository.EmployeeCommandRepository;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: crear un empleado.
 * Garantiza la unicidad de {@code documentId} y {@code email} antes de persistir.
 */
@Service
public class CreateEmployeeUseCase {

    private final EmployeeQueryRepository queryRepository;
    private final EmployeeCommandRepository commandRepository;

    public CreateEmployeeUseCase(EmployeeQueryRepository queryRepository,
                                 EmployeeCommandRepository commandRepository) {
        this.queryRepository = queryRepository;
        this.commandRepository = commandRepository;
    }

    @Transactional
    public Employee create(CreateEmployeeCommand command) {
        if (queryRepository.findByDocumentId(command.documentId()).isPresent()) {
            throw new DuplicateEmployeeException("documentId", command.documentId());
        }
        if (queryRepository.findByEmail(command.email()).isPresent()) {
            throw new DuplicateEmployeeException("email", command.email());
        }

        Employee employee = new Employee(
                null,
                command.firstName(),
                command.lastName(),
                command.documentId(),
                command.email(),
                command.position(),
                command.hireDate(),
                command.status() != null ? command.status() : EmployeeStatus.ACTIVE);

        return commandRepository.save(employee);
    }
}
