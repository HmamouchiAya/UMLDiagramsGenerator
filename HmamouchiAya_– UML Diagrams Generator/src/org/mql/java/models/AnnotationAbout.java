package org.mql.java.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Inherited;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnotationAbout {
    private String annotationName;
    private RetentionPolicy policy;
    private boolean inheritanceStatus;
    private Map<String, String> methodProperties;

    public AnnotationAbout(String annotationLocation) {
        methodProperties = new HashMap<>();
        try {
            Class<?> targetClass = Class.forName(annotationLocation);
            annotationName = targetClass.getSimpleName();
            Retention retention = targetClass.getAnnotation(Retention.class);
            policy = (retention != null) ? retention.value() : RetentionPolicy.CLASS;
            inheritanceStatus = targetClass.isAnnotationPresent(Inherited.class);
            
            for (Method method : targetClass.getDeclaredMethods()) {
                methodProperties.put(method.getName(), method.getReturnType().getSimpleName());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public RetentionPolicy getPolicy() {
        return policy;
    }

    public boolean inheritanceStatus() {
        return inheritanceStatus;
    }

    public Map<String, String> getMethodProperties() {
        return methodProperties;
    }

    @Override
    public String toString() {
        return "AnnotationInfo{" +
                "annotationName='" + annotationName + '\'' +
                ", policy=" + policy +
                ", inheritanceStatus=" + inheritanceStatus +
                ", methodProperties=" + methodProperties +
                '}';
    }
}