package com.example.employeemanagement;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeManagementApplicationTests {
    @Autowired
    private EmployeeService employeeService;

    @Test
    void createAndRetrieveEmployee() {
        String email = "test-" + System.nanoTime() + "@example.com";
        EmployeeRequest request = new EmployeeRequest(
                "Asha", "Nair", email, "Engineering", new BigDecimal("65000.00"), true);

        EmployeeResponse created = employeeService.create(request);
        EmployeeResponse retrieved = employeeService.findById(created.id());

        assertThat(retrieved.id()).isEqualTo(created.id());
        assertThat(retrieved.email()).isEqualTo(email);
        assertThat(retrieved.department()).isEqualTo("Engineering");
    }
}
