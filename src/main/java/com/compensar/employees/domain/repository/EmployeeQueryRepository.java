package com.compensar.employees.domain.repository;

import com.compensar.employees.domain.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de consultas (lectura) de empleados. Cada caso de uso depende solo
 * de los métodos que utiliza (ISP).
 */
public interface EmployeeQueryRepository {

    Optional<Employee> findById(Long id);

    Optional<Employee> findByDocumentId(String documentId);

    Optional<Employee> findByEmail(String email);

    List<Employee> findAll();
}
