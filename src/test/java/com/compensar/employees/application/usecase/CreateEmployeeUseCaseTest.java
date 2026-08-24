package com.compensar.employees.application.usecase;

import com.compensar.employees.application.dto.CreateEmployeeCommand;
import com.compensar.employees.domain.exception.DuplicateEmployeeException;
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
class CreateEmployeeUseCaseTest {

    @Mock
    private EmployeeQueryRepository queryRepository;

    @Mock
    private EmployeeCommandRepository commandRepository;

    @InjectMocks
    private CreateEmployeeUseCase useCase;

    private final CreateEmployeeCommand command = new CreateEmployeeCommand(
            "Ana", "Gómez", "CC-1001", "ana.gomez@example.com",
            "Software Engineer", LocalDate.of(2023, 3, 15), null);

    @Test
    void create_withValidCommand_defaultsStatusToActiveAndSaves() {
        when(queryRepository.findByDocumentId(command.documentId())).thenReturn(Optional.empty());
        when(queryRepository.findByEmail(command.email())).thenReturn(Optional.empty());
        when(commandRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee created = useCase.create(command);

        assertThat(created.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        verify(commandRepository).save(created);
    }

    @Test
    void create_whenDocumentIdAlreadyExists_throwsDuplicate() {
        when(queryRepository.findByDocumentId(command.documentId()))
                .thenReturn(Optional.of(new Employee(1L, "Otro", "Empleado", "CC-1001",
                        "otro@example.com", null, LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE)));

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(DuplicateEmployeeException.class)
                .hasMessageContaining("documentId");

        verify(commandRepository, never()).save(any());
    }

    @Test
    void create_whenEmailAlreadyExists_throwsDuplicate() {
        when(queryRepository.findByDocumentId(command.documentId())).thenReturn(Optional.empty());
        when(queryRepository.findByEmail(command.email()))
                .thenReturn(Optional.of(new Employee(2L, "Otro", "Empleado", "CC-2000",
                        "ana.gomez@example.com", null, LocalDate.of(2020, 1, 1), EmployeeStatus.ACTIVE)));

        assertThatThrownBy(() -> useCase.create(command))
                .isInstanceOf(DuplicateEmployeeException.class)
                .hasMessageContaining("email");

        verify(commandRepository, never()).save(any());
    }
}
