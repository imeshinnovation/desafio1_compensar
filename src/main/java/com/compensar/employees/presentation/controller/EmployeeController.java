package com.compensar.employees.presentation.controller;

import com.compensar.employees.application.dto.CreateEmployeeCommand;
import com.compensar.employees.application.dto.UpdateEmployeeCommand;
import com.compensar.employees.application.usecase.CreateEmployeeUseCase;
import com.compensar.employees.application.usecase.DeleteEmployeeUseCase;
import com.compensar.employees.application.usecase.GetEmployeeUseCase;
import com.compensar.employees.application.usecase.ListEmployeesUseCase;
import com.compensar.employees.application.usecase.UpdateEmployeeUseCase;
import com.compensar.employees.domain.model.Employee;
import com.compensar.employees.presentation.dto.CreateEmployeeRequest;
import com.compensar.employees.presentation.dto.EmployeeResponse;
import com.compensar.employees.presentation.dto.UpdateEmployeeRequest;
import com.compensar.employees.presentation.mapper.EmployeeRequestMapper;
import com.compensar.employees.presentation.mapper.EmployeeResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST del CRUD de empleados.
 * El controlador no contiene lógica de negocio: delega en casos de uso
 * y expone únicamente DTOs (nunca entidades de dominio o JPA).
 */
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final GetEmployeeUseCase getEmployeeUseCase;
    private final ListEmployeesUseCase listEmployeesUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;

    public EmployeeController(CreateEmployeeUseCase createEmployeeUseCase,
                              GetEmployeeUseCase getEmployeeUseCase,
                              ListEmployeesUseCase listEmployeesUseCase,
                              UpdateEmployeeUseCase updateEmployeeUseCase,
                              DeleteEmployeeUseCase deleteEmployeeUseCase) {
        this.createEmployeeUseCase = createEmployeeUseCase;
        this.getEmployeeUseCase = getEmployeeUseCase;
        this.listEmployeesUseCase = listEmployeesUseCase;
        this.updateEmployeeUseCase = updateEmployeeUseCase;
        this.deleteEmployeeUseCase = deleteEmployeeUseCase;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        CreateEmployeeCommand command = EmployeeRequestMapper.toCreateCommand(request);
        Employee created = createEmployeeUseCase.create(command);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(EmployeeResponseMapper.toResponse(created));
    }

    @GetMapping
    public List<EmployeeResponse> list() {
        return listEmployeesUseCase.listAll().stream()
                .map(EmployeeResponseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse get(@PathVariable Long id) {
        return EmployeeResponseMapper.toResponse(getEmployeeUseCase.getById(id));
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable Long id,
                                   @Valid @RequestBody UpdateEmployeeRequest request) {
        UpdateEmployeeCommand command = EmployeeRequestMapper.toUpdateCommand(request);
        return EmployeeResponseMapper.toResponse(updateEmployeeUseCase.update(id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteEmployeeUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
