package org.mql.java.models;

import org.mql.java.annotations.AssociationType;

public class AssociationAbout {
    private String sourceClass;
    private String targetClass;
    private String sourceClassName;
    private String targetClassName;
    private String associationType;
    private String lowerBound;
    private String upperBound;
    
    public AssociationAbout() {
        
    }
    
    public AssociationAbout(String sourceClass, String targetClass, String associationType) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.associationType = associationType;
        
        extractClassNames(sourceClass, targetClass);
    }
    
    private void extractClassNames(String source, String target) {
        String[] sourceParts = source.split("\\.");
        String[] targetParts = target.split("\\.");
        
        int sourceLength = sourceParts.length;
        int targetLength = targetParts.length;
        
        this.sourceClassName = sourceLength > 0 ? sourceParts[sourceLength - 1] : "";
        this.targetClassName = targetLength > 0 ? targetParts[targetLength - 1] : "";
    }

    
    public String getSourceClass() {
        return sourceClass;
    }
    
    public String getTargetClass() {
        return targetClass;
    }
    
    public String getSourceClassName() {
        return sourceClassName;
    }
    
    public String getTargetClassName() {
        return targetClassName;
    }
    
    public String getAssociationType() {
        return associationType;
    }
    
    public String getLowerBound() {
        return lowerBound;
    }
    
    public String getUpperBound() {
        return upperBound;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }
    
    public void setTargetClass(String targetClass) {
        this.targetClass = targetClass;
    }
    
    public void setSourceClassName(String sourceClassName) {
        this.sourceClassName = sourceClassName;
    }
    
    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }
    
    public void setAssociationType(String associationType) {
        this.associationType = associationType;
    }
    
    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }
    
    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }
}