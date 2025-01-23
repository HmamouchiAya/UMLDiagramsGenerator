package org.mql.java.models;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodAbout {
    private String methodName;
    private String outputType;
    private String arguments;
    private char modifier;
    
    public MethodAbout() {
       
    }
    
    public MethodAbout(Method method) {
        methodName = method.getName();
        outputType = method.getReturnType().getSimpleName();
        modifier = translateModifier(method.getModifiers());
        arguments = formatArguments(method);
    }

    private String formatArguments(Method method) {
        return "(" + 
            Arrays.stream(method.getParameterTypes())
                  .map(argType -> argType.getSimpleName())
                  .collect(Collectors.joining(", ")) +
            ")";
    }

    private char translateModifier(int modifier) {
        String modifierString = Modifier.toString(modifier);
        
        if (modifierString.contains("public")) return '+';
        if (modifierString.contains("private")) return '-';
        if (modifierString.contains("protected")) return '#';
        return '~';
    }
    
    public String getFormattedMethod() {
        return modifier + " " + methodName + arguments + " : " + outputType;
    }

   
    public String getMethodName() {
        return methodName;
    }

    public String getOutputType() {
        return outputType;
    }

    public String getArguments() {
        return arguments;
    }

    public char getModifer() {
        return modifier;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setModifier(char modifier) {
        this.modifier = modifier;
    }
}