package com.compensar.employees.infrastructure.persistence.repository;

import com.compensar.employees.infrastructure.persistence.entity.WorkShiftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JpaWorkShiftRepository extends JpaRepository<WorkShiftEntity, Long> {

    List<WorkShiftEntity> findByEmployeeIdOrderByDateAscStartTimeAsc(Long employeeId);

    List<WorkShiftEntity> findByEmployeeIdAndDateBetweenOrderByDateAscStartTimeAsc(
            Long employeeId, LocalDate from, LocalDate to);

    List<WorkShiftEntity> findByEmployeeIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(
            Long employeeId, LocalDate from);

    List<WorkShiftEntity> findByEmployeeIdAndDateLessThanEqualOrderByDateAscStartTimeAsc(
            Long employeeId, LocalDate to);
}
