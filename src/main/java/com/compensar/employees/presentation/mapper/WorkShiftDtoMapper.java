package com.compensar.employees.presentation.mapper;

import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.presentation.dto.WorkShiftResponse;

public final class WorkShiftDtoMapper {

    private WorkShiftDtoMapper() {
    }

    public static WorkShiftResponse toResponse(WorkShift workShift) {
        return new WorkShiftResponse(
                workShift.getId(),
                workShift.getEmployeeId(),
                workShift.getDate(),
                workShift.getStartTime(),
                workShift.getEndTime(),
                workShift.hoursWorked());
    }
}
