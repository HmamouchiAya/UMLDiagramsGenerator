package org.mql.java.examples;

public abstract class OrganizationMember {
    protected String name;
    protected String email;

    public OrganizationMember(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public abstract void displayInfo();
}
