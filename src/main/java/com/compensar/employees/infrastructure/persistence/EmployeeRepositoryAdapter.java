package com.compensar.employees.infrastructure.persistence;

import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.repository.EmployeeCommandRepository;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import com.compensar.employees.infrastructure.persistence.entity.EmployeeEntity;
import com.compensar.employees.infrastructure.persistence.mapper.EmployeeEntityMapper;
import com.compensar.employees.infrastructure.persistence.repository.JpaEmployeeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los puertos de empleados ({@link EmployeeQueryRepository}
 * y {@link EmployeeCommandRepository}) sobre Spring Data JPA.
 */
@Component
public class EmployeeRepositoryAdapter implements EmployeeQueryRepository, EmployeeCommandRepository {

    private final JpaEmployeeRepository jpaRepository;
    private final EmployeeEntityMapper mapper;

    public EmployeeRepositoryAdapter(JpaEmployeeRepository jpaRepository,
                                     EmployeeEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findByDocumentId(String documentId) {
        return jpaRepository.findByDocumentId(documentId).map(mapper::toDomain);
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Employee> findAll() {
        return jpaRepository.findAllByOrderByLastNameAsc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        if (employee.getId() != null) {
            // El modelo de dominio no modela la colección de jornadas: al hacer merge
            // con una colección vacía, orphanRemoval eliminaría las jornadas existentes.
            // Se preservan desde la entidad persistida antes de guardar.
            jpaRepository.findById(employee.getId())
                    .ifPresent(existing -> entity.setWorkShifts(existing.getWorkShifts()));
        }
        EmployeeEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(Employee employee) {
        jpaRepository.delete(mapper.toEntity(employee));
    }
}
