package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequest;
import com.example.employeemanagement.dto.EmployeeResponse;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.exception.DuplicateEmailException;
import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        ensureEmailAvailable(request.email(), null);
        Employee employee = new Employee(request.firstName().trim(), request.lastName().trim(),
                request.email().trim().toLowerCase(), request.department().trim(),
                request.salary(), request.activeOrDefault());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    public Page<EmployeeResponse> findAll(Pageable pageable, String search, Boolean active, String department) {
        Page<Employee> employees;
        if (active != null) {
            employees = employeeRepository.findByActive(active, pageable);
        } else if (StringUtils.hasText(department)) {
            employees = employeeRepository.findByDepartmentIgnoreCase(department.trim(), pageable);
        } else if (StringUtils.hasText(search)) {
            String term = search.trim();
            employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    term, term, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }
        return employees.map(EmployeeResponse::from);
    }

    public EmployeeResponse findById(Long id) {
        return EmployeeResponse.from(getEmployee(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = getEmployee(id);
        ensureEmailAvailable(request.email(), id);
        employee.updateDetails(request.firstName().trim(), request.lastName().trim(),
                request.email().trim().toLowerCase(), request.department().trim(),
                request.salary(), request.activeOrDefault());
        return EmployeeResponse.from(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = getEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private void ensureEmailAvailable(String email, Long currentEmployeeId) {
        employeeRepository.findByEmailIgnoreCase(email.trim()).ifPresent(existing -> {
            if (!existing.getId().equals(currentEmployeeId)) {
                throw new DuplicateEmailException(email);
            }
        });
    }
}
