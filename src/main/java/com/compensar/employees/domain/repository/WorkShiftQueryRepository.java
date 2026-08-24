package com.compensar.employees.domain.repository;

import com.compensar.employees.domain.model.WorkShift;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de consultas (lectura) de jornadas laborales.
 */
public interface WorkShiftQueryRepository {

    /**
     * Lista las jornadas de un empleado dentro de un rango de fechas (inclusive).
     * {@code from} y {@code to} pueden ser {@code null} para no acotar ese extremo.
     */
    List<WorkShift> findByEmployeeIdAndDateRange(Long employeeId, LocalDate from, LocalDate to);
}
