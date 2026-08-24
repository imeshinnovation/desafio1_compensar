package com.compensar.employees.domain.repository;

import com.compensar.employees.domain.model.WorkShift;

/**
 * Puerto de comandos (escritura) de jornadas laborales.
 */
public interface WorkShiftCommandRepository {

    WorkShift save(WorkShift workShift);
}
