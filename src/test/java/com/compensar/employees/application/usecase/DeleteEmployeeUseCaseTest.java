package com.compensar.employees.application.usecase;

import com.compensar.employees.domain.exception.EmployeeNotFoundException;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.domain.model.EmployeeStatus;
import com.compensar.employees.domain.repository.EmployeeCommandRepository;
import com.compensar.employees.domain.repository.EmployeeQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteEmployeeUseCaseTest {

    @Mock
    private EmployeeQueryRepository queryRepository;

    @Mock
    private EmployeeCommandRepository commandRepository;

    @InjectMocks
    private DeleteEmployeeUseCase useCase;

    @Test
    void delete_whenEmployeeDoesNotExist_throwsNotFound() {
        when(queryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(commandRepository, never()).delete(any());
    }

    @Test
    void delete_whenEmployeeExists_deletesIt() {
        Employee employee = new Employee(1L, "Ana", "Gómez", "CC-1001", "ana.gomez@example.com",
                null, LocalDate.of(2023, 3, 15), EmployeeStatus.ACTIVE);
        when(queryRepository.findById(1L)).thenReturn(Optional.of(employee));

        useCase.delete(1L);

        verify(commandRepository).delete(employee);
    }
}
