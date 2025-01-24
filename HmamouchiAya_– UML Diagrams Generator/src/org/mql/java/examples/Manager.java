package org.mql.java.examples;

import java.util.List;
import java.util.Vector;

class Manager extends Employee {
    private List<Employee> directReports;

    public Manager(String name, String email, double salary) {
        super(name, email, salary, EmployeeRole.MANAGER);
        this.directReports = new Vector<>();
    }

    public void addDirectReport(Employee employee) {
        directReports.add(employee);
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }
}
