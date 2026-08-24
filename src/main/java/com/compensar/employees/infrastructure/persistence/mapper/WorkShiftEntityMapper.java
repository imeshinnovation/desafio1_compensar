package com.compensar.employees.infrastructure.persistence.mapper;

import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.infrastructure.persistence.entity.EmployeeEntity;
import com.compensar.employees.infrastructure.persistence.entity.WorkShiftEntity;
import org.springframework.stereotype.Component;

@Component
public class WorkShiftEntityMapper {

    public WorkShiftEntity toEntity(WorkShift workShift, EmployeeEntity employee) {
        WorkShiftEntity entity = new WorkShiftEntity();
        entity.setId(workShift.getId());
        entity.setEmployee(employee);
        entity.setDate(workShift.getDate());
        entity.setStartTime(workShift.getStartTime());
        entity.setEndTime(workShift.getEndTime());
        return entity;
    }

    public WorkShift toDomain(WorkShiftEntity entity) {
        return WorkShift.of(
                entity.getId(),
                entity.getEmployee().getId(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime());
    }
}
