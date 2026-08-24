package com.compensar.employees.domain.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class WorkShift {

    private Long id;
    private Long employeeId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private WorkShift(Long id, Long employeeId, LocalDate date,
                      LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static WorkShift create(Long employeeId, LocalDate date,
                                   LocalTime startTime, LocalTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        return new WorkShift(null, employeeId, date, startTime, endTime);
    }

    public static WorkShift of(Long id, Long employeeId, LocalDate date,
                               LocalTime startTime, LocalTime endTime) {
        return new WorkShift(id, employeeId, date, startTime, endTime);
    }

    public double hoursWorked() {
        return Duration.between(startTime, endTime).toMinutes() / 60.0;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
