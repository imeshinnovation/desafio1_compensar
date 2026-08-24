package com.compensar.employees.infrastructure.persistence.mapper;

import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEntityMapper {

    public EmployeeEntity toEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(employee.getId());
        entity.setFirstName(employee.getFirstName());
        entity.setLastName(employee.getLastName());
        entity.setDocumentId(employee.getDocumentId());
        entity.setEmail(employee.getEmail());
        entity.setPosition(employee.getPosition());
        entity.setHireDate(employee.getHireDate());
        entity.setStatus(employee.getStatus());
        return entity;
    }

    public Employee toDomain(EmployeeEntity entity) {
        return new Employee(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDocumentId(),
                entity.getEmail(),
                entity.getPosition(),
                entity.getHireDate(),
                entity.getStatus());
    }
}
