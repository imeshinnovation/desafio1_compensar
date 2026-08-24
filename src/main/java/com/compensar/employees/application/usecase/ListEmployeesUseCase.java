package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso: listar todos los empleados.
 */
@Service
public class ListEmployeesUseCase {

    private final EmployeeQueryRepository queryRepository;

    public ListEmployeesUseCase(EmployeeQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> listAll() {
        return queryRepository.findAll();
    }
}
