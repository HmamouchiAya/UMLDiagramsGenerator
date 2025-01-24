package org.mql.java.examples;

import java.util.List;
import java.util.Vector;

public class Project {
    private String projectName;
    private List<Employee> teamMembers;
    private Manager projectManager;

    public Project(String projectName, Manager projectManager) {
        this.projectName = projectName;
        this.projectManager = projectManager;
        this.teamMembers = new Vector<>();
    }

    public void addTeamMember(Employee employee) {
        teamMembers.add(employee);
    }

    public void setProjectManager(Manager manager) {
        this.projectManager = manager;
    }
}
