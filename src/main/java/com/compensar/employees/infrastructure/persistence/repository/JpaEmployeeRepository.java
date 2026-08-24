package com.compensar.employees.infrastructure.persistence.repository;

import com.compensar.employees.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaEmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByDocumentId(String documentId);

    Optional<EmployeeEntity> findByEmail(String email);

    List<EmployeeEntity> findAllByOrderByLastNameAsc();
}
