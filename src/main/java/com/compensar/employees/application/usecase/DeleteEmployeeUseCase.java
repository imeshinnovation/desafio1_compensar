package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.repository.EmployeeCommandRepository;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: eliminar un empleado.
 * Sus jornadas laborales se eliminan en cascada.
 */
@Service
public class DeleteEmployeeUseCase {

    private final EmployeeQueryRepository queryRepository;
    private final EmployeeCommandRepository commandRepository;

    public DeleteEmployeeUseCase(EmployeeQueryRepository queryRepository,
                                 EmployeeCommandRepository commandRepository) {
        this.queryRepository = queryRepository;
        this.commandRepository = commandRepository;
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = queryRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        commandRepository.delete(employee);
    }
}
