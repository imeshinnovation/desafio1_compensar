package com.compensar.employees.infrastructure.persistence;

import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.domain.repository.WorkShiftCommandRepository;
import com.compensar.employees.domain.repository.WorkShiftQueryRepository;
import com.compensar.employees.infrastructure.persistence.entity.EmployeeEntity;
import com.compensar.employees.infrastructure.persistence.entity.WorkShiftEntity;
import com.compensar.employees.infrastructure.persistence.mapper.WorkShiftEntityMapper;
import com.compensar.employees.infrastructure.persistence.repository.JpaEmployeeRepository;
import com.compensar.employees.infrastructure.persistence.repository.JpaWorkShiftRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación de los puertos de jornadas ({@link WorkShiftQueryRepository}
 * y {@link WorkShiftCommandRepository}) sobre Spring Data JPA.
 */
@Component
public class WorkShiftRepositoryAdapter implements WorkShiftQueryRepository, WorkShiftCommandRepository {

    private final JpaWorkShiftRepository jpaRepository;
    private final JpaEmployeeRepository jpaEmployeeRepository;
    private final WorkShiftEntityMapper mapper;

    public WorkShiftRepositoryAdapter(JpaWorkShiftRepository jpaRepository,
                                      JpaEmployeeRepository jpaEmployeeRepository,
                                      WorkShiftEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.jpaEmployeeRepository = jpaEmployeeRepository;
        this.mapper = mapper;
    }

    @Override
    public List<WorkShift> findByEmployeeIdAndDateRange(Long employeeId, LocalDate from, LocalDate to) {
        List<WorkShiftEntity> entities = findEntities(employeeId, from, to);
        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public WorkShift save(WorkShift workShift) {
        EmployeeEntity employee = jpaEmployeeRepository.getReferenceById(workShift.getEmployeeId());
        WorkShiftEntity saved = jpaRepository.save(mapper.toEntity(workShift, employee));
        return mapper.toDomain(saved);
    }

    private List<WorkShiftEntity> findEntities(Long employeeId, LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return jpaRepository.findByEmployeeIdAndDateBetweenOrderByDateAscStartTimeAsc(employeeId, from, to);
        }
        if (from != null) {
            return jpaRepository.findByEmployeeIdAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(employeeId, from);
        }
        if (to != null) {
            return jpaRepository.findByEmployeeIdAndDateLessThanEqualOrderByDateAscStartTimeAsc(employeeId, to);
        }
        return jpaRepository.findByEmployeeIdOrderByDateAscStartTimeAsc(employeeId);
    }
}
