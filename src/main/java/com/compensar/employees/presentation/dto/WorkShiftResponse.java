package com.compensar.employees.presentation.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record WorkShiftResponse(
        Long id,
        Long employeeId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        double hoursWorked) {
}
