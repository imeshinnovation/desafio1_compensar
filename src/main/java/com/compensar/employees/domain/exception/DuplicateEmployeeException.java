package com.compensar.employees.domain.exception;

public class DuplicateEmployeeException extends RuntimeException {

    public DuplicateEmployeeException(String field, String value) {
        super("Employee with " + field + " '" + value + "' already exists");
    }
}
