package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: obtener un empleado por su id.
 */
@Service
public class GetEmployeeUseCase {

    private final EmployeeQueryRepository queryRepository;

    public GetEmployeeUseCase(EmployeeQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return queryRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
}
