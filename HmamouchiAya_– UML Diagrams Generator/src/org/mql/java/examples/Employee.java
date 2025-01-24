package org.mql.java.examples;

import java.util.List;
import java.util.Vector;

class Employee extends OrganizationMember implements Comparable<Employee> {
    private double salary;
    private EmployeeRole role;
    private Department department;
    private List<Project> assignedProjects;

    public Employee(String name, String email, double salary, EmployeeRole role) {
        super(name, email);
        this.salary = salary;
        this.role = role;
        this.assignedProjects = new Vector<>();
    }

    public void assignToDepartment(Department department) {
        this.department = department;
        department.addEmployee(this);
    }

    public void assignToProject(Project project) {
        assignedProjects.add(project);
        project.addTeamMember(this);
    }

    @Override
    public void displayInfo() {
        System.out.println("Employee: " + name + ", Role: " + role);
    }

    @Override
    public int compareTo(Employee other) {
        return Double.compare(this.salary, other.salary);
    }
}

