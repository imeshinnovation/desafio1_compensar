package com.compensar.employees.application.usecase;

import com.compensar.employees.application.dto.UpdateEmployeeCommand;
import com.compensar.employees.domain.exception.DuplicateEmployeeException;
import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.repository.EmployeeCommandRepository;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

/**
 * Caso de uso: actualizar (reemplazar) el perfil de un empleado.
 * Verifica unicidad de {@code documentId} y {@code email} excluyendo al propio empleado.
 */
@Service
public class UpdateEmployeeUseCase {

    private final EmployeeQueryRepository queryRepository;
    private final EmployeeCommandRepository commandRepository;

    public UpdateEmployeeUseCase(EmployeeQueryRepository queryRepository,
                                 EmployeeCommandRepository commandRepository) {
        this.queryRepository = queryRepository;
        this.commandRepository = commandRepository;
    }

    @Transactional
    public Employee update(Long id, UpdateEmployeeCommand command) {
        Employee employee = queryRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        assertUniqueExceptSelf(queryRepository::findByDocumentId, command.documentId(), "documentId", id);
        assertUniqueExceptSelf(queryRepository::findByEmail, command.email(), "email", id);

        employee.updateProfile(
                command.firstName(),
                command.lastName(),
                command.documentId(),
                command.email(),
                command.position(),
                command.hireDate(),
                command.status());

        return commandRepository.save(employee);
    }

    private void assertUniqueExceptSelf(Function<String, Optional<Employee>> finder,
                                        String value, String field, Long selfId) {
        finder.apply(value)
                .filter(other -> !other.getId().equals(selfId))
                .ifPresent(other -> {
                    throw new DuplicateEmployeeException(field, value);
                });
    }
}
