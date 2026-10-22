package com.example.employeemanagement.dto;

import com.example.employeemanagement.entity.Employee;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String department,
        BigDecimal salary,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(), employee.getFirstName(), employee.getLastName(),
                employee.getEmail(), employee.getDepartment(), employee.getSalary(),
                employee.isActive(), employee.getCreatedAt(), employee.getUpdatedAt()
        );
    }
}
