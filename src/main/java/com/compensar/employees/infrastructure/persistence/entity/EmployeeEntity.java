package com.compensar.employees.infrastructure.persistence.entity;

import com.compensar.employees.domain.model.EmployeeStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employees_document_id", columnNames = "document_id"),
        @UniqueConstraint(name = "uk_employees_email", columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    private String documentId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 100)
    private String position;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkShiftEntity> workShifts = new ArrayList<>();
}
