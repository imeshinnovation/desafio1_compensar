package com.compensar.employees.application.usecase;

import com.compensar.employees.application.dto.UpdateEmployeeCommand;
import com.compensar.employees.domain.exception.DuplicateEmployeeException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateEmployeeUseCaseTest {

    @Mock
    private EmployeeQueryRepository queryRepository;

    @Mock
    private EmployeeCommandRepository commandRepository;

    @InjectMocks
    private UpdateEmployeeUseCase useCase;

    private static final Long EMPLOYEE_ID = 1L;

    private final Employee existing = new Employee(EMPLOYEE_ID, "Ana", "Gómez", "CC-1001",
            "ana.gomez@example.com", "Software Engineer", LocalDate.of(2023, 3, 15),
            EmployeeStatus.ACTIVE);

    private final UpdateEmployeeCommand command = new UpdateEmployeeCommand(
            "Ana María", "Gómez Ruiz", "CC-1001", "ana.gomez@example.com",
            "Senior Software Engineer", LocalDate.of(2023, 3, 15), EmployeeStatus.INACTIVE);

    @Test
    void update_whenEmployeeDoesNotExist_throwsNotFound() {
        when(queryRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(EMPLOYEE_ID, command))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void update_whenDocumentIdBelongsToAnotherEmployee_throwsDuplicate() {
        when(queryRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(existing));
        when(queryRepository.findByDocumentId(command.documentId()))
                .thenReturn(Optional.of(new Employee(2L, "Otro", "Empleado", "CC-1001",
                        "otro@example.com", null, LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE)));

        assertThatThrownBy(() -> useCase.update(EMPLOYEE_ID, command))
                .isInstanceOf(DuplicateEmployeeException.class);

        verify(commandRepository, never()).save(any());
    }

    @Test
    void update_whenEmailBelongsToAnotherEmployee_throwsDuplicate() {
        when(queryRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(existing));
        when(queryRepository.findByDocumentId(command.documentId())).thenReturn(Optional.empty());
        when(queryRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(new Employee(2L, "Otro", "Empleado", "CC-2000",
                        "ana.gomez@example.com", null, LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE)));

        assertThatThrownBy(() -> useCase.update(EMPLOYEE_ID, command))
                .isInstanceOf(DuplicateEmployeeException.class);

        verify(commandRepository, never()).save(any());
    }

    @Test
    void update_withValidCommand_updatesProfileAndSaves() {
        when(queryRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(existing));
        when(queryRepository.findByDocumentId(command.documentId())).thenReturn(Optional.of(existing));
        when(queryRepository.findByEmail(command.email())).thenReturn(Optional.of(existing));
        when(commandRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee updated = useCase.update(EMPLOYEE_ID, command);

        assertThat(updated.getFirstName()).isEqualTo("Ana María");
        assertThat(updated.getPosition()).isEqualTo("Senior Software Engineer");
        assertThat(updated.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
        verify(commandRepository).save(existing);
    }
}
