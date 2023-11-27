package com.technology.employee.repositories;

import com.technology.employee.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
    Optional<Employee> findEmployeeByEmail(String email);
}
