package org.mql.java.models;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class FieldAbout {
    private Field fieldElement;
    private String fieldName;
    private String fieldType;
    private String simpleTypeName;
    private char modifier;
    private boolean isUserDefined;
    private boolean isList;
    
    public FieldAbout() {
        
    }

    public FieldAbout(Field fieldElement) {
        this.fieldElement = fieldElement;
        fieldName = fieldElement.getName();
        fieldType = fieldElement.getType().getName();
        simpleTypeName = fieldElement.getType().getSimpleName();
        modifier = calculateAccessLevel(fieldElement.getModifiers());
        
        if (List.class.isAssignableFrom(fieldElement.getType())) {
            processListType(fieldElement);
            this.isUserDefined = true;
            this.isList = true;
        } else {
            this.isUserDefined = !fieldElement.getType().isPrimitive() 
                             && !fieldElement.getType().getName().startsWith("java");
            this.isList = false;
        }
    }
    
    private void processListType(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0) {
                this.fieldType = typeArgs[0].getTypeName();
            }
        }
    }
    
    private char calculateAccessLevel(int modifier) {
        String modifierStr = Modifier.toString(modifier);
        
        if (modifierStr.contains("public")) return '+';
        if (modifierStr.contains("private")) return '-';
        if (modifierStr.contains("protected")) return '#';
        return '~';
    }
    
    public String getFormattedString() {
        return modifier + " " + fieldName + " : " + simpleTypeName;
    }

    public Field getFieldElement() {
        return this.fieldElement;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public char getModifier() {
        return modifier;
    }
    
    public boolean isUserDefined() {
        return isUserDefined;
    }
    
    public boolean isList() {
        return isList;
    }

    public void setFieldElement(Field fieldElement) {
        this.fieldElement = fieldElement;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public void setSimpleTypeName(String simpleTypeName) {
        this.simpleTypeName = simpleTypeName;
    }

    public void setModifier(char modifier) {
        this.modifier = modifier;
    }

    public void setUserDefined(boolean isUserDefined) {
        this.isUserDefined = isUserDefined;
    }
}