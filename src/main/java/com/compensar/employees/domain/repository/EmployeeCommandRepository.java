package com.compensar.employees.domain.repository;

import com.compensar.employees.domain.model.Employee;

/**
 * Puerto de comandos (escritura) de empleados, segregado del de lectura (CQS).
 */
public interface EmployeeCommandRepository {

    Employee save(Employee employee);

    void delete(Employee employee);
}
