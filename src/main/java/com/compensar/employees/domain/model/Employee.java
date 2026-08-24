package com.compensar.employees.domain.model;

import java.time.LocalDate;

public class Employee {

    private Long id;
    private String firstName;
    private String lastName;
    private String documentId;
    private String email;
    private String position;
    private LocalDate hireDate;
    private EmployeeStatus status;

    public Employee(Long id, String firstName, String lastName, String documentId,
                    String email, String position, LocalDate hireDate, EmployeeStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = documentId;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = status;
    }

    public void updateProfile(String firstName, String lastName, String documentId,
                              String email, String position, LocalDate hireDate, EmployeeStatus status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentId = documentId;
        this.email = email;
        this.position = position;
        this.hireDate = hireDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }
}
