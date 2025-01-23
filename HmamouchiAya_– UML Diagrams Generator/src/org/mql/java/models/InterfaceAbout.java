package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import java.util.List;

public class InterfaceAbout {
    private String simpleName;
    private String name;
    private String modifiers;
    private String superClass;
    private List<FieldAbout> fields;
    private List<MethodAbout> methods;

    public InterfaceAbout(Class<?> cls) {
        this.simpleName = cls.getSimpleName();
        this.name = cls.getName();
        initializeClassInfo(cls);
    }
    
    public InterfaceAbout(String path) throws ClassNotFoundException {
        this(Class.forName(path));
    }
    
    public InterfaceAbout() {
        fields = new Vector<>();
        methods = new Vector<>();
    }

    private void initializeClassInfo(Class<?> cls) {
        getModifiers(cls);
        getSuperClass(cls);
        getFields(cls);    
        getMethods(cls);
    }

    private void getSuperClass(Class<?> cls) {
        if (cls.getSuperclass() == null) {
            superClass = null;
            return;
        }
        superClass = cls.getSuperclass().getName();
        
        if ("java.lang.Object".equals(superClass)) {
            superClass = null;
        }
    }
    
    private void getModifiers(Class<?> cls) {
        modifiers = Modifier.toString(cls.getModifiers());
    }
    
    private void getFields(Class<?> cls) {
        fields = new Vector<>();
        Field[] declaredFields = cls.getDeclaredFields();
        for (Field field : declaredFields) {
        	fields.add(new FieldAbout(field));
        }
    }
    
    private void getMethods(Class<?> cls) {
        methods = new Vector<>();
        Method[] declaredMethods = cls.getDeclaredMethods();
        for (Method method : declaredMethods) {
            methods.add(new MethodAbout(method));
        }
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getName() {
        return name;
    }

    public String getModifiers() {
        return modifiers;
    }

    public String superClass() {
        return superClass;
    }

    public List<FieldAbout> getFields() {
        return fields;
    }

    public List<MethodAbout> getMethods() {
        return methods;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}