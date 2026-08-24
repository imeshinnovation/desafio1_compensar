package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.model.EmployeeStatus;
import com.compensar.employees.domain.model.WorkShift;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import com.compensar.employees.domain.repository.WorkShiftQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListWorkShiftsUseCaseTest {

    @Mock
    private EmployeeQueryRepository employeeQueryRepository;

    @Mock
    private WorkShiftQueryRepository workShiftQueryRepository;

    @InjectMocks
    private ListWorkShiftsUseCase useCase;

    private final Employee employee = new Employee(1L, "Ana", "Gómez", "CC-1001",
            "ana.gomez@example.com", null, LocalDate.of(2023, 3, 15), EmployeeStatus.ACTIVE);

    private final WorkShift mondayShift = WorkShift.of(1L, 1L,
            LocalDate.of(2026, 8, 17), LocalTime.of(8, 0), LocalTime.of(17, 0));

    private final WorkShift fridayShift = WorkShift.of(2L, 1L,
            LocalDate.of(2026, 8, 21), LocalTime.of(8, 0), LocalTime.of(17, 0));

    @Test
    void listByEmployee_whenEmployeeDoesNotExist_throwsNotFound() {
        when(employeeQueryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.listByEmployee(99L, null, null))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(workShiftQueryRepository, never()).findByEmployeeIdAndDateRange(any(), any(), any());
    }

    @Test
    void listByEmployee_withDateRange_delegatesFilteringToRepository() {
        LocalDate from = LocalDate.of(2026, 8, 19);
        LocalDate to = LocalDate.of(2026, 8, 21);
        when(employeeQueryRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(workShiftQueryRepository.findByEmployeeIdAndDateRange(1L, from, to))
                .thenReturn(List.of(fridayShift));

        List<WorkShift> result = useCase.listByEmployee(1L, from, to);

        assertThat(result).containsExactly(fridayShift);
    }

    @Test
    void listByEmployee_withoutRange_returnsAllShifts() {
        when(employeeQueryRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(workShiftQueryRepository.findByEmployeeIdAndDateRange(1L, null, null))
                .thenReturn(List.of(mondayShift, fridayShift));

        List<WorkShift> result = useCase.listByEmployee(1L, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).hoursWorked()).isEqualTo(9.0);
    }
}
