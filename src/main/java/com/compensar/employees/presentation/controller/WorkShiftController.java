package com.compensar.employees.presentation.controller;

import com.compensar.employees.application.usecase.ListWorkShiftsUseCase;
import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.presentation.dto.WorkShiftResponse;
import com.compensar.employees.presentation.mapper.WorkShiftDtoMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints REST de las jornadas laborales de un empleado.
 */
@RestController
@RequestMapping("/api/v1/employees/{employeeId}/work-shifts")
public class WorkShiftController {

    private final ListWorkShiftsUseCase listWorkShiftsUseCase;

    public WorkShiftController(ListWorkShiftsUseCase listWorkShiftsUseCase) {
        this.listWorkShiftsUseCase = listWorkShiftsUseCase;
    }

    @GetMapping
    public List<WorkShiftResponse> list(
            @PathVariable Long employeeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        assertValidDateRange(from, to);
        List<WorkShift> workShifts = listWorkShiftsUseCase.listByEmployee(employeeId, from, to);
        return workShifts.stream()
                .map(WorkShiftDtoMapper::toResponse)
                .toList();
    }

    private void assertValidDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }
    }
}
