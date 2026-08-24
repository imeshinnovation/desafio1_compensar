package com.compensar.employees.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EmployeeControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/employees";

    private static final String EXTERNAL_DB_URL = System.getenv("TEST_EXTERNAL_DB_URL");

    private static final PostgreSQLContainer<?> POSTGRES = initPostgres();

    private static PostgreSQLContainer<?> initPostgres() {
        if (EXTERNAL_DB_URL != null) {
            return null;
        }
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:17-alpine")
                .withDatabaseName("employees")
                .withUsername("employees")
                .withPassword("employees")
                .withInitScript("init.sql");
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        if (POSTGRES == null) {
            registry.add("spring.datasource.url", () -> EXTERNAL_DB_URL);
            registry.add("spring.datasource.username",
                    () -> System.getenv("TEST_EXTERNAL_DB_USERNAME"));
            registry.add("spring.datasource.password",
                    () -> System.getenv("TEST_EXTERNAL_DB_PASSWORD"));
        } else {
            registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", POSTGRES::getUsername);
            registry.add("spring.datasource.password", POSTGRES::getPassword);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createEmployee_returns201WithLocationAndBody() throws Exception {
        String body = """
                {
                  "firstName": "Pedro",
                  "lastName": "Suárez",
                  "documentId": "CC-9999",
                  "email": "pedro.suarez@example.com",
                  "position": "QA Engineer",
                  "hireDate": "2025-01-20"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.firstName").value("Pedro"))
                .andExpect(jsonPath("$.documentId").value("CC-9999"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void listEmployees_returnsSeededEmployees() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(30));
    }

    @Test
    void getEmployee_returnsSeededEmployee() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Gómez"));
    }

    @Test
    void getEmployee_whenNotFound_returns404WithConsistentErrorBody() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + "/9999"))
                .andExpect(status().isNotFound())
                .andReturn();

        JsonNode error = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(error.get("status").asInt()).isEqualTo(404);
        assertThat(error.get("error").asText()).isEqualTo("Not Found");
        assertThat(error.get("message").asText()).contains("9999");
        assertThat(error.get("path").asText()).isEqualTo(BASE_URL + "/9999");
        assertThat(error.get("timestamp")).isNotNull();
    }

    @Test
    void updateEmployee_replacesProfile() throws Exception {
        String body = """
                {
                  "firstName": "Ana Carolina",
                  "lastName": "Gómez",
                  "documentId": "CC-1001",
                  "email": "ana.gomez@example.com",
                  "position": "Staff Software Engineer",
                  "hireDate": "2023-03-15",
                  "status": "ACTIVE"
                }
                """;

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ana Carolina"))
                .andExpect(jsonPath("$.position").value("Staff Software Engineer"));
    }

    @Test
    void updateEmployee_preservesExistingWorkShifts() throws Exception {
        String body = """
                {
                  "firstName": "Ana",
                  "lastName": "Gómez",
                  "documentId": "CC-1001",
                  "email": "ana.gomez@example.com",
                  "position": "Software Engineer",
                  "hireDate": "2023-03-15",
                  "status": "ACTIVE"
                }
                """;

        MvcResult before = mockMvc.perform(get(BASE_URL + "/1/work-shifts"))
                .andExpect(status().isOk())
                .andReturn();
        int shiftsBefore = objectMapper.readTree(before.getResponse().getContentAsString()).size();

        mockMvc.perform(put(BASE_URL + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        MvcResult after = mockMvc.perform(get(BASE_URL + "/1/work-shifts"))
                .andExpect(status().isOk())
                .andReturn();
        int shiftsAfter = objectMapper.readTree(after.getResponse().getContentAsString()).size();
        assertThat(shiftsAfter).isEqualTo(shiftsBefore);
    }

    @Test
    void deleteEmployee_returns204AndSubsequentGetIs404() throws Exception {
        String createdBody = """
                {
                  "firstName": "Temporal",
                  "lastName": "Empleado",
                  "documentId": "CC-TEMP",
                  "email": "temporal@example.com",
                  "position": "Intern",
                  "hireDate": "2026-01-01"
                }
                """;
        MvcResult created = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createdBody))
                .andExpect(status().isCreated())
                .andReturn();
        long id = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete(BASE_URL + "/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEmployee_whenDocumentIdDuplicate_returns409() throws Exception {
        String body = """
                {
                  "firstName": "Otra",
                  "lastName": "Persona",
                  "documentId": "CC-1001",
                  "email": "otra.persona@example.com",
                  "position": "Dev",
                  "hireDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Employee with documentId 'CC-1001' already exists"));
    }

    @Test
    void createEmployee_whenEmailInvalid_returns400() throws Exception {
        String body = """
                {
                  "firstName": "Mala",
                  "lastName": "Entrada",
                  "documentId": "CC-8888",
                  "email": "no-es-un-email",
                  "position": "Dev",
                  "hireDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(
                        "Validation failed: email: email must have a valid format"));
    }

    @Test
    void getEmployee_whenIdNotNumeric_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listWorkShifts_returnsShiftsWithConsistentShape() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL + "/1/work-shifts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        JsonNode shifts = objectMapper.readTree(result.getResponse().getContentAsString());
        if (shifts.isEmpty()) {

            return;
        }
        JsonNode first = shifts.get(0);
        assertThat(first.get("employeeId").asLong()).isEqualTo(1L);
        assertThat(first.get("date").asText()).matches("\\d{4}-\\d{2}-\\d{2}");
        double hoursWorked = first.get("hoursWorked").asDouble();
        double expected = Duration.between(
                LocalTime.parse(first.get("startTime").asText()),
                LocalTime.parse(first.get("endTime").asText())).toMinutes() / 60.0;
        assertThat(hoursWorked).isEqualTo(expected);
    }

    @Test
    void listWorkShifts_withDateRange_returnsOnlyShiftsWithinRange() throws Exception {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(90);

        MvcResult unfiltered = mockMvc.perform(get(BASE_URL + "/1/work-shifts"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult filtered = mockMvc.perform(get(BASE_URL + "/1/work-shifts")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode allShifts = objectMapper.readTree(unfiltered.getResponse().getContentAsString());
        JsonNode filteredShifts = objectMapper.readTree(filtered.getResponse().getContentAsString());

        assertThat(filteredShifts.size()).isEqualTo(allShifts.size());
        for (JsonNode shift : filteredShifts) {
            LocalDate date = LocalDate.parse(shift.get("date").asText());
            assertThat(date).isBetween(from, to);
        }
    }

    @Test
    void listWorkShifts_withInvalidRange_returns400() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1/work-shifts")
                        .param("from", "2026-08-21")
                        .param("to", "2026-08-19"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("from must be before or equal to to"));
    }

    @Test
    void listWorkShifts_whenEmployeeNotFound_returns404() throws Exception {
        mockMvc.perform(get(BASE_URL + "/9999/work-shifts"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee with id 9999 not found"));
    }
}
