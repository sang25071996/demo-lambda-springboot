package com.demo.lambda.service;

import com.demo.lambda.model.Employee;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

  public List<Employee> getEmployees() {
    return Arrays.asList(new Employee(1, "Tony", 25),
        new Employee(2, "Tom", 26),
        new Employee(3, "John", 27),
        new Employee(4, "Kelvin Bryne", 30),
        new Employee(5, "Bruno", 26));
  }
}
