package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import com.compensar.employees.domain.repository.WorkShiftQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso: listar las jornadas laborales de un empleado,
 * opcionalmente acotadas por rango de fechas.
 */
@Service
public class ListWorkShiftsUseCase {

    private final EmployeeQueryRepository employeeQueryRepository;
    private final WorkShiftQueryRepository workShiftQueryRepository;

    public ListWorkShiftsUseCase(EmployeeQueryRepository employeeQueryRepository,
                                 WorkShiftQueryRepository workShiftQueryRepository) {
        this.employeeQueryRepository = employeeQueryRepository;
        this.workShiftQueryRepository = workShiftQueryRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkShift> listByEmployee(Long employeeId, LocalDate from, LocalDate to) {
        employeeQueryRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return workShiftQueryRepository.findByEmployeeIdAndDateRange(employeeId, from, to);
    }
}
