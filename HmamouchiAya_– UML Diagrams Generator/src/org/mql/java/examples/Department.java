package org.mql.java.examples;

import java.util.List;
import java.util.Vector;

public class Department {
    private String departmentName;
    private DepartmentType type;
    private List<Employee> employees;
    private Manager departmentHead;

    public Department(String departmentName, DepartmentType type) {
        this.departmentName = departmentName;
        this.type = type;
        this.employees = new Vector<>();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void setDepartmentHead(Manager manager) {
        this.departmentHead = manager;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}