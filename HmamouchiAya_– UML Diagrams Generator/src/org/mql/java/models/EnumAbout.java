package org.mql.java.models;


import java.util.List;
import java.util.Vector;

public class EnumAbout {
    private final String qualifiedName;
    private final String simpleName;
    private final List<String> fields;

    public EnumAbout(String enumLocation) {
        try {
            Class<?> enumClass = loadEnumClass(enumLocation);
            this.qualifiedName = enumClass.getName();
            this.simpleName = enumClass.getSimpleName();
            this.fields = extractFields(enumClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to load enum class: " + enumLocation, e);
        }
    }

    private Class<?> loadEnumClass(String enumLocation) throws ClassNotFoundException {
        return Class.forName(enumLocation);
    }

    private List<String> extractFields(Class<?> enumClass) {
        List<String> constants = new Vector<>();
        Object[] enumValues = enumClass.getFields();
        
        if (enumValues != null) {
            for (Object constant : enumValues) {
                constants.add(constant.toString());
            }
        }
        
        return constants;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public List<String> getFields() {
        return new Vector<>(fields);
    }

    @Override
    public String toString() {
        return "EnumDetails{" +
               "qualifiedName='" + qualifiedName + '\'' +
               ", simpleName='" + simpleName + '\'' +
               ", enumConstants=" + fields +
               '}';
    }
}