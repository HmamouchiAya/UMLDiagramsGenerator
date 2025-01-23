package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

public class ClassAbout {
    private String simpleName;
    private String name;
    private String modifiers;
    private String superClass;
    private List<FieldAbout> fields;
    private List<MethodAbout> methods;
    private List<AssociationAbout> associations;
    private List<AssociationAbout> dependencyAssociations;
    private List<AssociationAbout> compositionAssociations;
    private List<AssociationAbout> aggregationAssociations;
    private List<InterfaceAbout> implementedInterfaces;

    public ClassAbout() {
        initializeLists();
    }

    public ClassAbout(String classLocation) {
        initializeLists();
        analyzeClass(classLocation);
    }

    private void initializeLists() {
        fields = new Vector<>();
        methods = new Vector<>();
        associations = new Vector<>();
        dependencyAssociations = new Vector<>();
        compositionAssociations = new Vector<>();
        aggregationAssociations = new Vector<>();
        implementedInterfaces = new Vector<>();
    }

    private void analyzeClass(String classLocation) {
        try {
            Class<?> cls = Class.forName(classLocation);
            extractBasicInfo(cls);
            extractRelations(cls);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void extractBasicInfo(Class<?> cls) {
        simpleName = cls.getSimpleName();
        name = cls.getName();
        getModifiers(cls);
        getSuperClass(cls);
        getFields(cls);
        getMethods(cls);
    }

    private void extractRelations(Class<?> cls) {
        getAssociations(fields);
        getDependencyAssociations(cls);
        getComposedClasses(fields);
        getAggregatedClasses(fields);
        getImplementedInterfaces(cls);
    }

    private void getSuperClass(Class<?> cls) {
        if (cls.getSuperclass() == null) return;
        superClass = cls.getSuperclass().getName();

        if ("java.lang.Object".equals(superClass)) {
            superClass = null;
        }
    }

    private void getModifiers(Class<?> cls) {
        modifiers = Modifier.toString(cls.getModifiers());
    }

    private void getFields(Class<?> cls) {
        for (Field field : cls.getDeclaredFields()) {
            fields.add(new FieldAbout(field));
        }
    }

    private void getMethods(Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            methods.add(new MethodAbout(method));
        }
    }

    private void getAssociations(List<FieldAbout> fields) {
        if (superClass != null) {
            associations.add(new AssociationAbout(name, superClass, "Inheritance"));
        }
    }

    private void getDependencyAssociations(Class<?> cls) {
        List<String> fieldTypes = new Vector<>();
        for (FieldAbout field : fields) {
            fieldTypes.add(field.getFieldType());
        }
        analyzeMethodDependencies(cls, fieldTypes);
    }

    private void analyzeMethodDependencies(Class<?> cls, List<String> fieldTypes) {
        for (Method method : cls.getDeclaredMethods()) {
            checkParameterTypes(cls, method, fieldTypes);
            checkReturnType(cls, method, fieldTypes);
        }
    }

    private void checkParameterTypes(Class<?> cls, Method method, List<String> fieldTypes) {
        for (Class<?> paramType : method.getParameterTypes()) {
            addUsedClassIfCustomType(cls, paramType, fieldTypes);
        }
    }

    private void checkReturnType(Class<?> cls, Method method, List<String> fieldTypes) {
        Class<?> returnType = method.getReturnType();
        addUsedClassIfCustomType(cls, returnType, fieldTypes);
    }

    private void addUsedClassIfCustomType(Class<?> cls, Class<?> type, List<String> fieldTypes) {
        if (isCustomType(type) && !fieldTypes.contains(type.getName()) && !relationExists(type.getName())) {
            AssociationAbout relation = new AssociationAbout(cls.getName(), type.getName(), "Use");
            dependencyAssociations.add(relation);
            associations.add(relation);
        }
    }

    private void getComposedClasses(List<FieldAbout> fields) {
        for (FieldAbout field : fields) {
            if (field.isUserDefined() && Modifier.isFinal(field.getFieldElement().getModifiers())) {
                addCompositionRelation(field);
            }
        }
    }

    private void addCompositionRelation(FieldAbout field) {
        AssociationAbout relation = new AssociationAbout(name, field.getFieldType(), "Composition");
        relation.setUpperBound(field.isList() ? "*" : "1");
        compositionAssociations.add(relation);
        associations.add(relation);
    }

    private void getAggregatedClasses(List<FieldAbout> fields) {
        for (FieldAbout field : fields) {
            if (field.isUserDefined() && !Modifier.isFinal(field.getFieldElement().getModifiers())) {
                addAggregationRelation(field);
            }
        }
    }

    private void addAggregationRelation(FieldAbout field) {
        AssociationAbout relation = new AssociationAbout(name, field.getFieldType(), "Aggregation");
        relation.setUpperBound(field.isList() ? "*" : "1");
        aggregationAssociations.add(relation);
        associations.add(relation);
    }

    private void getImplementedInterfaces(Class<?> cls) {
        for (Class<?> iface : cls.getInterfaces()) {
            implementedInterfaces.add(new InterfaceAbout(iface));
            associations.add(new AssociationAbout(name, iface.getName(), "Implementation"));
        }
    }

    private boolean relationExists(String to) {
        return dependencyAssociations.stream()
                .anyMatch(relation -> relation.getTargetClass().equals(to));
    }

    private boolean isCustomType(Class<?> cls) {
        return !cls.isPrimitive() && !cls.getName().startsWith("java.lang");
    }

    public String getSimpleName() { return simpleName; }
    public String getName() { return name; }
    public String getModifiers() { return modifiers; }
    public String getSuperClass() { return superClass; }
    public List<FieldAbout> getFields() { return fields; }
    public List<MethodAbout> getMethods() { return methods; }
    public List<AssociationAbout> getAssociations() { return associations; }
    public List<AssociationAbout> getDependencyAssociations() { return dependencyAssociations; }
    public List<AssociationAbout> getCompositionAssociations() { return compositionAssociations; }
    public List<AssociationAbout> getAggregationAssociations() { return aggregationAssociations; }
    public List<InterfaceAbout> getImplementedInterfaces() { return implementedInterfaces; }

    public void setName(String name) { this.name = name; }
    public void setModifiers(String modifiers) { this.modifiers = modifiers; }
    public void setSuperClass(String superClass) { this.superClass = superClass; }
}